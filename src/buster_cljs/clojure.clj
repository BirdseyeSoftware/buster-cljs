(ns buster-cljs.clojure
  (:require [clojure.test :as test]))

(defmacro deftest
  "Alias for clojure.test/deftest"
  [title & body]
  `(test/deftest ~title ~@body))

(defmacro describe
  "Alias for clojure.test/testing"
  [msg & body]
  `(test/testing ~msg ~@body))

(defmacro it
  "Alias for clojure.test/testing"
  [msg & body]
  `(test/testing ~msg ~@body))

(defmacro is
  "Re-export for clojure.test/is"
  [& args]
  `(test/is ~@args))
