(ns buster-cljs.macros)

(defmacro initialize-buster []
   '(if (not= "undefined" (js* "typeof(exports)"))
      (js* "buster = require(\"buster\")")))

(defmacro describe [desc & body]
  `(.describe (.-spec js/buster) ~(name desc)
              (fn []
                ~@body
                nil)))

(defmacro deftest [test-title & body]
  `(describe ~(name test-title) ~@body))

(defmacro it [desc & body]
  `(.it (.-spec js/buster) ~desc
         (fn []
           ~@body
           nil)))
