(defproject buster-cljs "0.1.0-SNAPSHOT"
  :description "buster.js clojurescript wrapper"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [potemkin "0.1.6"]]
  :plugins [[lein-cljsbuild "0.2.9"]]
  :source-paths ["src"]
  :cljsbuild
  {:builds
   [{:id "dev"
     :source-path "src"
     :compiler
     {:optimizations :simple
      :pretty-print true
      :externs ["resources/externs/buster.js"]
      :output-to "resources/js/buster_cljs_dev.js"}}
    ;;;
    {:id "browser-test"
     :source-path "test"
     :notify-command ["./resources/buster_runner.sh"]
     :compiler
     {:optimizations :simple
      :pretty-print true
      :externs ["resouces/externs/buster.js"]
      :libraries ["resources/js/buster_cljs_dev.js"]
      :output-to "resources/js/buster_cljs_browser_test.js"}}
    ;;;
    {:id "node-test"
     :source-path "test"
     :notify-command ["./resources/buster_runner.sh"]
     :compiler
     {:optimizations :simple
      :libraries ["resources/js/buster_cljs.js"]
      :externs ["resouces/externs/buster_dev.js"]
      :output-to "resources/js/buster_cljs_node_test.js"}}]})
