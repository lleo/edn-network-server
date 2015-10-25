(ns edn-network-server.core
  (:require [aleph.tcp :as tcp]
            [manifold.deferred :as d]
            [manifold.stream :as s]
            [byte-streams :as bs]
            [clojure.string :as string]
            [clojure.edn :as edn]
            [clojure.pprint :as pp]
            [cheshire.core :as ch]
            [lleo.utils :as utils]
            [edn-network-server.datastore :as ds]
            [me.raynes.fs :as fs])
  (:import (java.util.regex Pattern Matcher)
           (java.nio ByteBuffer))
  (:gen-class))

(def edn-msg-0 "{ :msg-id 0
 :cmd STORE
 :node { :host \"vm-fedora-0.local\" :subsys \"load-avg\" :id nil }
 :metric { :name \"1m\" :period 60000 :unit \"run-q-length\" }
 :time #inst \"1985-04-12T23:20:50.52Z\"
 :value 5.2 }

")

(def ok-rsp "{ :code :ok }\n\n")

(defn edn-read-print [s]
  (println (str "read:" s "."))
  (println (str "edn:" (pr-str (edn/read-string s)) "."))
  )

(defn json-read-print [s]
  (println (str "read:" s "."))
  (println (str "json:" (pr-str (ch/parse-string s true)) "."))
  )


(defn process_1 [input strm]
  (let [inpstr (bs/to-string input)]
    (println (str "recvd>" inpstr "<"))
    (edn-read-print (string/trim-newline inpstr))
    (s/put! strm ok-rsp)))

(defn process_2 [input strm]
  (let [inpstr (bs/to-string input)]
    (println (str "recvd>" inpstr "<"))
    (json-read-print (string/trim-newline inpstr))
    (s/put! strm ok-rsp)))

(let [curstr (atom "")]
  (defn- parse [newstr]
    (swap! curstr str newstr)
    (let [strs (utils/split-fixed @curstr "\n\n")
          res (butlast strs)
          las (last strs)
          ]
      (swap! curstr (fn [a b] b) las)
      res
      )
    )
  (defn- get-curstr [] @curstr)
  (defn- reset-parse [] (swap! curstr (fn [a b] b) ""))
  )

(defn- process_3 [input strm]
  (let [inpstr (bs/to-string input)]
    (println "class input:" (class input))
    (println "inpstr:>" inpstr "<")
    (count (for [msg (parse inpstr)]
             (if (nil? msg)
               (println "no input found")
               (let [jsonobj (ch/parse-string msg true)]
                 (print "jsonobj:")
                 (pp/pprint jsonobj)
                 (println)
                 msg))))
    (println "curstr:>" (pr-str (get-curstr)) "<")))

(defn- process_input [input strm])

(def PORT 9999)
(defn sk-handler [sk-strm info]
  (s/consume (fn [msg] (process_3 msg sk-strm)) sk-strm))


(defn -main
  "Read a edn string and convert it to a datastructure & print it"
  [& args]
  (println "Ready.")

;  (println "TESTING Utils LIBRARY")
;  (def s "foo\nbar\n")
;  (println "(def s \"foo\\nbar\\n\")")
;
;  (println "(split-fixed s \"\\n\") ->" (pr-str (utils/split-fixed s "\n")))
;  (println "(split-fixed-limit s \"\\n\" 2) ->" (pr-str (utils/split-fixed-limit s "\n" 2)))
;
;  (println "\nMulti-method split:")
;  (println "(split s \"\\n\") ->" (pr-str (utils/split s "\n")))
;  (println "(split s \"\\n\" 2) ->" (pr-str (utils/split s "\n" 2)))
;  (println "(split s #\"\\n\") ->" (pr-str (utils/split s #"\n")))
;  (println "(split s #\"\\n\" 2) ->" (pr-str (utils/split s #"\n" 2)))


  (println "\nTESTING datastore LIBRARY")

  (println "OPENING Datastore")
  (def ds (ds/open-datastore "./FOO.db"))


  (println "class:" (class ds))
  (println "type:" (type ds))

  (println "\nWRITING")

  (def bb (ByteBuffer/allocate 1024))

  (def lorem "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vestibulum at tortor augue. Phasellus orci sem, pellentesque eu nibh quis, elementum malesuada elit. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis volutpat.\n") ;253 bytes

  (.put bb (.getBytes lorem))
  (.flip bb)
  (ds/write ds bb)

  (println "\nREADING 1")
  (println "CONTENT\n=======")

  (.clear bb)
  (let [nbyts (ds/read ds bb 0) vbb (vec (.array bb))]
    (.flip bb)
    (println (String. (.array (.compact bb))))
    )

  (println "\nWRITING SMALL W/ OFFSET")
  (println "========================")
  
  (defn rotator-10 []
    (let [tick (atom -1)]
      #(swap! tick (comp (fn [x] (mod x 10)) inc))
      ))
  (defn rotate-numbers-string [n]
    (String.
     (byte-array
      (map (partial + 48)
           (take n (repeatedly (rotator-10)))))))
  
  (def small-bb (ByteBuffer/allocate 16))
  (.put small-bb (.getBytes (rotate-numbers-string 16)))
  (.flip small-bb)
  (.position (.fc ds) 0)
  (println (str "writing string: \"" (String. (.array small-bb)) "\""))
  (println "at offset: 8")
  (ds/write ds small-bb 8)

  (println "\nREADING 2")
  (println "CONTENT\n=======")
                                        ;
  (.clear bb)
  (let [nbyts (ds/read ds bb 0) vbb (vec (.array bb))]
    (.flip bb)
    (println (String. (.array (.compact bb))))
    )
  
  (println "CLOSING Datastore")
  (ds/close-datastore ds)
  
;  (println "\nSTARTING SERVER on port", PORT)
;  (tcp/start-server sk-handler {:port PORT})

  (println "done."))
