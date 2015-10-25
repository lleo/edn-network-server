(defproject edn-network-server "0.1.0-SNAPSHOT"
  :description "test network server inputing edn messages"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [aleph "0.4.0"]
                 [manifold "0.1.0"]
                 [byte-streams "0.2.0"]
                 [cheshire "5.5.0"]
                 [me.raynes/fs "1.4.6"]
                 [lleo/utils "0.1.0"]
                 ]
  :main ^:skip-aot edn-network-server.core
  :jvm-opts ["-server"]
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             }
  :eval-in :nrepl
  )
