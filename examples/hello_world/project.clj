(defproject hello_world "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.birdseye-sw/buster-cljs "0.1.0"]]
  :plugins [[lein-cljsbuild "0.2.9"]]
  :cljsbuild
  {:builds
   [{:id "dev"
     :source-path "src/"
     :compiler
     {:optimizations :whitespace
      :pretty-print true
      :output-to "resources/js/hello_world_dev.js"}}
    ;;
    {:id "browser-test"
     :source-path "test/"
     :compiler
     ;; IMPORTANT: optimizations :whitespace _won't work_
     {:optimizations :simple
      :pretty-print true
      :externs ["externs/buster.js"]
      :libraries ["resources/js/hello_world_dev.js"]
      :output-to "resources/js/hello_world_browser_test.js"}}
    ;;
    {:id "browser-optimized-test"
     :source-path "test/"
     :compiler
     {:optimizations :advanced
      :pretty-print false
      :externs ["externs/buster.js"]
      :libraries ["resources/js/hello_world_dev.js"]
      :output-to "resources/js/hello_world_browser_optimized_test.js"}}
    ;;
    {:id "node-test"
     :source-path "test/"
     :compiler
     {:optimizations :simple
      :pretty-print true
      :externs ["externs/buster.js"]
      :libraries ["resources/js/hello_world_dev.js"]
      :output-to "resources/js/hello_world_node_test.js"}}]})
