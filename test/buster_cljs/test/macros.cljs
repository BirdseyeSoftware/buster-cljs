(ns buster-cljs.test.macros
  (:require [buster-cljs.core :refer [is]])
  (:require-macros [buster-cljs.macros
                    :refer [initialize-buster deftest describe it]]))

(initialize-buster)

(defn fn-that-calls-testing []
  (it "testing inside function"
    (is true)))

(deftest buster-cljs-simple-macros-work
  (it "single level testing"
    (is (= 6 (reduce + [1 2 3])))))

(deftest buster-cljs-nested-macros-work
          (describe "top-level testing"
                    (it "inner-level 1"
                        (is true))
                    (it "inner-level inner-level 2"
                        (is true))))

(deftest buster-cljs-macros-nested-on-functions
  (fn-that-calls-testing))

(deftest buster-cljs-macros-inside-let-blocks
  (let [a "uno"
        b "uno"]
    (it "testing nested on a let block"
      (is (= a b)))))
