(ns buster-cljs.test.macros-test
  (:require-macros [buster-cljs.macros
                    :refer [initialize-buster deftest describe it is are]])
  (:require [buster-cljs.runtime :as _runtime]))

(initialize-buster)

(defn fn-that-calls-testing []
  (it "inside function"
    (is true)))

(deftest simple-it-macro-works
  (it "single level testing"
    (is (= 6 (reduce + [1 2 3])))))

(deftest nested-describe-it-macros-work
  (describe "top-level testing"
    (it "inner-level 1"
      (is true))
    (it "inner-level inner-level 2"
      (is true))))

(deftest macros-inside-functions-work
  (fn-that-calls-testing))

(deftest macros-inside-let
  (let [a "uno"
        b "uno"]
    (it "using a let block"
      (is (= a b)))))

(deftest is-macro-with-various-predicate-functions
  (describe "assertions with `='"
    (it "accepts two arguments"
      (is (= 1 1)))
    (it "accepts multiple arguments"
      (is (= 1 1 1 1))))

  (describe "assertions with `not='"
    (it "accepts two arguments"
      (is (not= 1 2)))
    (it "accepts multiple arguments"
      (is (not= 1 2 3))))

  (describe "assertions with `fn?'"
    (it "works"
      (is (fn? +) "fn? should work"))))

(deftest is-macro-with-exception-features
  (describe "assertions with `thrown?'"
    (it "simple example"
                (is (thrown? js/Error
                             (throw (js/Error. "an error")))))
    (it "using function"
                (letfn [(thrower [] (throw (js/Error. "this is an error")))]
                  (is (thrown? js/Error (thrower))))))

  (it "assertions with `thrown-with-msg?'"
    (is (thrown-with-msg? js/Error #"another error"
            (throw (js/Error. "another error"))))))

;; (deftest is-macro-with-eq
;;   (describe "assertions with `eq'"
;;     (it "simple example"
;;       #_(is (= 1 (inc 2)))
;;       (is (= {:hello "world"}
;;              (assoc {} :hola "mundo"))))))

;; doesn't work currently
;; fails with:
;;
;;   Compiling "resources/js/buster_cljs_node_test.js" failed.
;;   java.lang.AssertionError: Assert failed: Can't qualify symbol in catch
;;   (not (namespace name))
;;
;; when using single quote fails with:
;;
;;  WARNING: Use of undeclared Var buster-cljs.test.macros-test/body at line 70 test/buster_cljs/test/macros_test.cljs
;;  WARNING: Use of undeclared Var buster-cljs.test.macros-test/msg at line 72 test/buster_cljs/test/macros_test.cljs
;;

(deftest async-test
  ;; NOTE: this uses the internal async features from busterjs
  (it "should be async" :async
     (js/setTimeout #(done (is true)) 150)))

(deftest are-macro
  (it "should work correctly"
    (are [x y] (= x y)
         2 (+ 1 1)
         4 (* 2 2))))
