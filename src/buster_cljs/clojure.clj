(ns buster-cljs.clojure
  (:require [clojure.test :as test]
            [potemkin :refer [import-macro]]))

(import-macro test/deftest)
(import-macro test/is)
(import-macro test/are)

;;;
(defmacro describe
  "Alias for clojure.test/testing to match the way buster test
  hierarchies work"
  [msg & body]
  `(test/testing ~msg ~@body))

(defmacro it
  "Alias for clojure.test/testing to match the way buster test
  hierarchies work"
  [msg & body]
  `(test/testing ~msg ~@body))
