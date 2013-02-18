(defproject route-66 "0.1.0-SNAPSHOT"
  
  :description "Clojure library for routing web requests."
  :url "http://github.com/grammati/route-66"
  
  :license {:name "MIT License"
            :url "http://opensource.org/licenses/MIT"}
  
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [clout "1.1.0"]
                 [compojure "1.1.5"]]

  :profiles {:dev {:dependencies [[ring-mock "0.1.3"]]}})
