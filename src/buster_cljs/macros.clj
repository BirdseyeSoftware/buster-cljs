(ns buster-cljs.macros
  ^{:author "Roman Gonzalez with recommendations of Tavis Rudd."}
  (:require [cljs.core]
            [clojure.string :as str]))

(defmacro initialize-buster
  "Setup buster for testing code in node.js"
  []
  '(if (not= "undefined" (js* "typeof(exports)"))
     (js* "buster = require(\"buster\")")))

(defmacro deftest
  "Defines a test function with no arguments. This is an alias for the
   `describe' function, is just here for compatibility with the
   clojure.test library"
  [test-title & body]
  `(describe ~(str/replace (clojure.core/name test-title) #"-" " ") ~@body))

(defmacro describe
  "Defines a context in which a test is happening, you may nest
   multiple describe calls."
  [desc & body]
  `(.describe (.-spec js/buster) ~(clojure.core/name desc)
              (fn []
                ~@body
                nil)))

(defmacro it
  "Final context definition where the tests are going to be executed,
  if an `it' call is not specified, tests won't be shown on the result
  output of the test suite"
  [desc & body]
  `(.it (.-spec js/buster) ~desc
        (fn []
          ~@body
          nil)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;; ASSERTION METHODS

;; You don't call these, but you can add methods to extend the 'is'
;; macro. These define different kinds of tests, based on the first
;; symbol in the test expression.

(defmulti assert-expr
  (fn [msg form]
    (cond
      (nil? form) :always-fail
      (seq? form) (first form)
      :else :default)))

;; ;;;;;;;;;;;;;;;;;;;;

(defn assert-predicate
  ""
  [msg form]
  (let [args (rest form)
        pred (first form)]
    `(let [values# (cljs.core/list ~@args)
           result# (cljs.core/apply ~pred values#)
           msg# (and ~msg (cljs.core/str ~msg ". "))]
       (.assert js/buster result# (str msg# "Expected " '~form ", got (not " '~form ")")))))

(defn assert-any
  ""
  [msg form]
  `(let [value# ~form
         msg# (and ~msg (str ~msg ". "))]
     (.assert js/buster value# (str msg# "Expected " '~form ", got " value#))))

(defmethod assert-expr :default
  [msg form]
  (if (and (sequential? form) (fn? (first form)))
    (assert-predicate msg form)
    (assert-any msg form)))

(defmethod assert-expr :always-fail
  [msg form]
  `(.assert js/buster false ~msg))

;;
;; Leaving comments here for future blogspot about macros and try/catch clauses
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


(defmethod assert-expr 'thrown? [msg form]
  (let [e (gensym "e")
        error-type (second form)
        body (nthnext form 2)
        msg (and msg (str msg ". "))]
    `(~'try*
       ~@body
       (.assert js/buster false (cljs.core/str ~msg "Expected error to be thrown."))
       (catch ~e
           (buster-cljs.macros/is (instance? ~error-type ~e))))))

(defmethod assert-expr 'thrown-with-msg? [msg form]
  (let [e (gensym "e")
        error-type (nth form 1)
        re (nth form 2)
        body (nthnext form 3)
        msg (and msg (str msg ". "))]
    `(~'try*
       ~@body
       (.assert js/buster false (cljs.core/str ~msg "Expected error to be thrown."))
       (catch ~e
           (buster-cljs.macros/is (cljs.core/instance? ~error-type ~e))
         (buster-cljs.macros/is (cljs.core/re-find ~re (.-message ~e)))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defmacro is
  "Generic assertion macro. 'form' is any predicate test.
'msg' is an optional message to attach to the assertion.
Example: (is (= 4 (+ 2 2)) \"Two plus two should be 4\")

Special forms:

(is (thrown? c body)) checks that an instance of c is
thrown from body, fails if not; then returns the thing
thrown.

(is (thrown-with-msg? c re body)) checks that an instance
of c is thrown AND that the message on the exception
matches (with re-find) the regular expression re."
  ([form] `(buster-cljs.macros/is ~form nil))
  ([form msg]
     `~(assert-expr msg form)))