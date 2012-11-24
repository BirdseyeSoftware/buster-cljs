(ns buster-cljs.macros
  (:refer-clojure :exclude [try])
  (:require [cljs.core :refer [try]]
            [clojure.string :as str]))

(defmacro initialize-buster []
   '(if (not= "undefined" (js* "typeof(exports)"))
      (js* "buster = require(\"buster\")")))

(defmacro deftest [test-title & body]
  `(describe ~(str/replace (name test-title) #"-" " ") ~@body))

(defmacro describe [desc & body]
  `(.describe (.-spec js/buster) ~(name desc)
              (fn []
                ~@body
                nil)))

(defmacro it [desc & body]
  `(.it (.-spec js/buster) ~desc
         (fn []
           ~@body
           nil)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmulti assert-expr
  (fn [msg form]
    (cond
      (nil? form) :always-fail
      (seq? form) (first form)
      :else :default)))

;; ;;;;;;;;;;;;;;;;;;;;

(defn assert-predicate [msg form]
  (let [args (rest form)
        pred (first form)]
    `(let [values# (list ~@args)
           result# (apply ~pred values#)
           msg# (and ~msg (str ~msg ". "))]
       (.assert js/buster result# (str msg# "Expected " '~form ", got (not " '~form ")")))))

(defn assert-any [msg form]
  `(let [value# ~form
         msg# (and ~msg (str ~msg ". "))]
     (.assert js/buster value# (str msg# "Expected " '~form ", got " value#))))

(defmethod assert-expr :default [msg form]
  (if (and (sequential? form) (fn? (first form)))
    (assert-predicate msg form)
    (assert-any msg form)))

(defmethod assert-expr :always-fail [msg form]
  `(.assert js/buster false ~msg))

;; ;; doesn't work currently
;; ;; fails with:
;; ;;
;; ;;   Compiling "resources/js/buster_cljs_node_test.js" failed.
;; ;;   java.lang.AssertionError: Assert failed: Can't qualify symbol in catch
;; ;;   (not (namespace name))
;; ;;
;; ;; when using single quote fails with:
;; ;;
;; ;;  WARNING: Use of undeclared Var buster-cljs.test.macros-test/body at line 70 test/buster_cljs/test/macros_test.cljs
;; ;;  WARNING: Use of undeclared Var buster-cljs.test.macros-test/msg at line 72 test/buster_cljs/test/macros_test.cljs
;; ;;
;; (defmethod assert-expr 'thrown? [msg form]
;;   (let [error-type (second form)
;;         body (nthnext form 2)
;;         msg (and msg (str msg ". "))]
;;     `(try
;;        ~@body
;;        (.assert js/buster false (str ~msg "Expected error to be thrown."))
;;        ;; (catch ~error-type e
;;        (catch ~error-type _
;;          (.assert js/buster true)))))

;; (defmethod assert-expr 'thrown-with-msg? [msg form]
;;   (let [error-type (nth form 1)
;;         re (nth form 2)
;;         body (nthnext form 3)
;;         msg (and msg (str msg ". "))]
;;     `(try
;;        ~@body
;;        (.assert js/buster false (str ~msg "Expected error to be thrown."))
;;        (catch ~error-type e
;;          (.assert js/buster (re-find ~re (.-message e)))))))

;;;;;;;;;;;;;;;;;;;;

(defmacro is
  ([form] `(is ~form nil))
  ([form msg]
     `~(assert-expr msg form)))
