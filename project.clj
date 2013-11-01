(defproject com.birdseye-sw/buster-cljs "0.1.4"
  :description "Crossbrowser/Crossplatform Clojurescript testing"
  :url "http://birdseye-sw.com/oss/buster-cljs/"
  :license {:name "MIT"
            :url "http://opensource.org/licenses/MIT"}
  :scm {:name "git"
        :url "https://github.com/BirdseyeSoftware/buster-cljs"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/clojurescript "0.0-1934"]
                 [potemkin "0.3.3"]]
  :plugins [[lein-cljsbuild "0.3.4"]]
  :source-paths ["src"]
  :hooks [leiningen.cljsbuild]
  :cljsbuild
  {:test-commands {"buster" ["./run-buster-tests.sh"]}
   :builds
   [{:id "browser-test"
     :source-paths ["src" "test"]
     :compiler
     {:optimizations :whitespace
      :pretty-print true
      :target :browser
      :externs ["resources/externs/buster.js"]
      :output-to "resources/js/buster_cljs_browser_test.js"}}
    {:id "browser-test-optimized"
     :source-paths ["src" "test"]
     :compiler
     {:optimizations :advanced
      :pretty-print false
      :target :browser
      :externs ["resources/externs/buster.js"]
      :output-to "resources/js/buster_cljs_browser_test.js"}}
    {:id "node-test"
     :source-paths ["src" "test"]
     :compiler
     {:optimizations :simple
      :target :node
      :externs ["resources/externs/buster.js"]
      :output-to "resources/js/buster_cljs_node_test.js"}}]})
