(ns buster-cljs.clojure
  (:require [clojure.test :refer [testing]]))

(defmacro describe [msg & body]
  `(testing ~msg ~@body))

(defmacro it [msg & body]
  `(testing ~msg ~@body))
