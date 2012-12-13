(ns hello-world.core-test
  (:require [hello-world.core :as hello])
  (:require-macros [buster-cljs.macros :refer [initialize-buster deftest describe it is are]]))

;; NOTE: This call is important if you run your test-suite against node
(initialize-buster)

(deftest test-say-hello
  (it "say-hello works correctly"
      (are [in out] (= (hello/say-hello in) out)
           :english  "Hello World"
           :spanish  "Hola Mundo"
           :german   "Hallo Welt"
           :french   "Bonjour tout le monde")))