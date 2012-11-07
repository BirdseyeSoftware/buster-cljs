(ns buster-cljs.test.macros
  (:require [buster-cljs.core :refer [cljs->js is]])
  (:require-macros [buster-cljs.macros
                    :refer [deftest testing]]))

(if (not= "undefined" (js* "typeof(exports)"))
     (js* "buster = require(\"buster\")"))

(deftest buster-cljs-macros-work
  (testing "dummy test 1 to check source transformation"
    (is (= 6 (reduce + [1 2 3])))))
