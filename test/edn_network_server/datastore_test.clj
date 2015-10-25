(ns edn-network-server.datastore-test
  (:require [clojure.test :refer :all]
            [edn-network-server.datastore :refer :all]))


(deftest open-datastore-test
  (testing "Construct a Datastore"
    (is (instance? edn_network_server.datastore.Datastore 
                   (def DS (open-datastore "./TEST.db"))))))

(deftest close-datastore-test
  (testing "Close a Datastore"
    (is (nil? (try (close-datastore DS) (catch Exception e e))))))
