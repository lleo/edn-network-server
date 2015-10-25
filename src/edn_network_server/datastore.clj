(ns edn-network-server.datastore
  (:require [lleo.utils :as utils]
            [me.raynes.fs :as fs]
            [clojure.string :as string])
  (:import (java.io RandomAccessFile)
           (java.nio ByteBuffer)
           (java.nio.file Paths StandardOpenOption)
           (java.nio.channels FileChannel)
           ))

(defprotocol Store
  
  (read
    [this bytebuffer offset]
    [this bytebuffer]
    "Read offset-length bytes into bytebuffer starting at bb_pos offset into bytebuffer"
    )
  
  (write
    [this bytebuffer offset]
    [this bytebuffer]
    "Write [bb_pos,length) bytes from bytebuffer into datastore starting at offset"
    )
  )

;; Constructor ->Datastore automagically created by deftype
;;
(deftype Datastore [fc path]
  Store

  (read [this bytebuffer]
      (.read (.fc this) bytebuffer)
    )
  
  (read [this bytebuffer offset]
    (.read (.fc this) bytebuffer offset)
    )    

  (write [this bytebuffer]
    (.write (.fc this) bytebuffer)
    )

  (write [this bytebuffer offset]
    (.write (.fc this) bytebuffer offset)
    ) 
  )

(defn to-path [str-path]
  (let [split-path (fs/split str-path)]    
    (Paths/get
     (first split-path)
     (into-array (rest split-path)))))

(defn open-datastore [str-path]
  (->Datastore
   (FileChannel/open
    (to-path str-path)
    (into-array [StandardOpenOption/READ StandardOpenOption/WRITE
                 StandardOpenOption/CREATE StandardOpenOption/SYNC]))
   (to-path str-path)))

(defn close-datastore [ds]
  (let [result (try
                 (.close (.fc ds))
                 (catch Exception e e))]
    (if (instance? Exception result)
      (print "Close FAILED:" (.getMessage result))
      nil
      )))
