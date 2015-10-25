
(set-env!
 :source-paths #{"src"}
 :dependencies '[[org.clojure/clojure "1.7.0"]
                 [aleph "0.4.0"]
                 [manifold "0.1.0"]
                 [byte-streams "0.2.0"]
                 [cheshire "5.5.0"]
                 [me.raynes/fs "1.4.6"]
                 [lleo/utils "0.1.0"]
                 ]
 )

(task-options!
 aot {:namespace '#{edn-network-server.core
                    edn-network-server.datastore}}
 pom {:project 'edn_network_server
      :version "0.1.0"
      }
 jar {:main 'edn_network_server.core})

(deftask build
  "Builds an uberjar of this project that can be run with java -jar"
  []
  (comp
   (aot)
   (pom)
   (uber)
   (jar)))

;;(deftask build
;;  "Builds an uberjar of this project that can be run with java -jar"
;;  []
;;  (comp
;;   (aot :namespace '#{edn-network-server})
;;   (pom :project 'edn-network-server
;;        :version "1.0.0")
;;   (uber)
;;   (jar :main 'edn-network-server)))
