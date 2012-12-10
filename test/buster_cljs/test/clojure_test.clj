(ns buster-cljs.test.clojure-test
  (:require
   [clojure.test :as test]
   [buster-cljs.clojure :refer [deftest is are it describe]]))

(deftest wrapper-macros
  (describe "`it` and `describe` are wrappers for clojure.test/testing"
    (is true)
    (it "works"
      (is true)
      (describe "even when nested"
        (is true)))))

(defmacro is-same-macro [var1 var2]
  `(let [keys# [:macro :line :file :doc]]
    (is (= (select-keys (meta #'~var1) keys#)
           (select-keys (meta #'~var2) keys#)))))

(deftest import-mapper
  (describe "`is`, `are`, and `deftest` are simple import mappers"
    (is-same-macro clojure.test/deftest buster-cljs.clojure/deftest)
    (is-same-macro clojure.test/are buster-cljs.clojure/are)
    (is-same-macro clojure.test/is buster-cljs.clojure/is)))
