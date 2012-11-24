(ns buster-cljs.clojure
  (:require [clojure.test :as test]))

(defmacro deftest [title & body]
  `(test/deftest ~title ~@body))

(defmacro describe [msg & body]
  `(test/testing ~msg ~@body))

(defmacro it [msg & body]
  `(test/testing ~msg ~@body))

(defmacro is [& args]
  `(test/is ~@args))
