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
  `(if (and (goog.isDefAndNotNull "window")
            (not (goog.isDefAndNotNull "window.buster"))
            (goog.isDefAndNotNull "window.buster_cljs_drop_test_on_missing_buster"))
     nil
     ;; else
     (describe ~(str/replace (clojure.core/name test-title) #"-" " ") ~@body)))

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
  (condp = (first body)
    :async `(.it (.-spec js/buster) ~desc
                 (fn [~'done]
                   ~@(rest body)
                   nil))
    `(.it (.-spec js/buster) ~desc
          (fn []
            ~@body
            nil))))

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

(defmethod assert-expr '= [msg form]
  (let [[a b] (rest form)]
    `(let [msg# (and ~msg (cljs.core/str ~msg ". "))
           a-result# ~a
           b-result# ~b]
       (if (and (coll? a-result#)
                (coll? b-result#))
         ;; use-diff
         (let [[only-a# only-b# both# :as result#] (buster-cljs.runtime/diff a-result# b-result#)]
           (if (and (empty? only-a#)
                    (empty? only-b#))
             (.assert js/buster true)
             (.assert js/buster false
                      (cljs.core/str msg# "Expected " '~a " (a) to be equal to " '~b " (b), got: "
                                     "\n  only (a): " (pr-str only-a#)
                                     "\n  only (b): " (pr-str only-b#)
                                     "\n  both (a) and (b): " (pr-str both#)))))
         ;; else use =
         (if (cljs.core/= a-result# b-result#)
           (.assert js/buster true)
           (.assert js/buster false
                    (cljs.core/str msg# "Expected to be equal but got: "
                                   "\n  expected: " (pr-str a-result#)
                                   "\n  got: " (pr-str b-result#))))))))

(defmethod assert-expr 'thrown? [msg form]
  (let [e (gensym "e")
        error-type (second form)
        body (nthnext form 2)
        msg (and msg (str msg ". "))
        ]
    `(try
       ~@body
       (.assert js/buster false (cljs.core/str ~msg "Expected error to be thrown."))
       (catch ~error-type ~e
         (.assert js/buster true)))))

(defmethod assert-expr 'thrown-with-msg? [msg form]
  (let [e (gensym "e")
        error-type (nth form 1)
        re (nth form 2)
        body (nthnext form 3)
        msg (and msg (str msg ". "))]
    `(try
       ~@body
       (.assert js/buster false (cljs.core/str ~msg "Expected error to be thrown."))
       (catch ~error-type ~e
         (.assert js/buster true)))))

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

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Port of clojure.test/are macro
;; for that we will need to migrate functions:
;;
;; * clojure.walk/walk
;; * clojure.walk/prewalk
;; * clojure.walk/prewalk-replace
;; * clojure.template/apply-template
;; * clojure.test/do-template
;;

(defn walk
  "Traverses form, an arbitrary data structure. inner and outer are
functions. Applies inner to each element of form, building up a
data structure of the same type, then applies outer to the result.
Recognizes all Clojure data structures. Consumes seqs as with doall."

  {:added "1.1"}
  [inner outer form]
  (cond
    (list? form) (outer (apply list (map inner form)))
    (instance? clojure.lang.IMapEntry form) (outer (vec (map inner form)))
    (seq? form) (outer (doall (map inner form)))
    (coll? form) (outer (into (empty form) (map inner form)))
    :else (outer form)))

(defn prewalk
  "Like postwalk, but does pre-order traversal."
  {:added "1.1"}
  [f form]
    (walk (partial prewalk f) identity (f form)))

(defn prewalk-replace
  "Recursively transforms form by replacing keys in smap with their
values. Like clojure/replace but works on any data structure. Does
replacement at the root of the tree first."
  {:added "1.1"}
  [smap form]
    (prewalk (fn [x] (if (contains? smap x) (smap x) x)) form))

(defn apply-template
  "For use in macros. argv is an argument list, as in defn. expr is
a quoted expression using the symbols in argv. values is a sequence
of values to be used for the arguments.

apply-template will recursively replace argument symbols in expr
with their corresponding values, returning a modified expr.

Example: (apply-template '[x] '(+ x x) '[2])
;=> (+ 2 2)"
  [argv expr values]
  (assert (vector? argv))
  (assert (every? symbol? argv))
  (prewalk-replace (zipmap argv values) expr))

(defmacro do-template
  "Repeatedly copies expr (in a do block) for each group of arguments
in values. values are automatically partitioned by the number of
arguments in argv, an argument vector as in defn.

Example: (macroexpand '(do-template [x y] (+ y x) 2 4 3 5))
;=> (do (+ 4 2) (+ 5 3))"
  [argv expr & values]
  (let [c (count argv)]
    `(do ~@(map (fn [a] (apply-template argv expr a))
                                (partition c values)))))

(defmacro are
  "Checks multiple assertions with a template expression.
See clojure.template/do-template for an explanation of
templates.

Example: (are [x y] (= x y)
2 (+ 1 1)
4 (* 2 2))
Expands to:
(do (is (= 2 (+ 1 1)))
    (is (= 4 (* 2 2))))

Note: This breaks some reporting features, such as line numbers. "
  {:added "1.1 "}
  [argv expr & args]
  (if (or
       ;; (are [] true) is meaningless but ok
       (and (empty? argv) (empty? args))
       ;; Catch wrong number of args
       (and (pos? (count argv))
            (pos? (count args))
            (zero? (mod (count args) (count argv)))))
    `(do-template ~argv (is ~expr) ~@args)
    (throw (new IllegalArgumentException "The number of args doesn't match are's argv."))))
