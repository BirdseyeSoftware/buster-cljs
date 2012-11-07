(ns buster-cljs.macros)

(defmacro initialize-buster []
   `(if (not= "undefined" (js* "typeof(exports)"))
      (js* "buster = require(\"buster\")")))

(defmacro deftest [desc & body]
  `(let [test-map# (-> {} ~@body)]
     (.testCase js/buster ~(name desc)
        (buster-cljs.assertions/cljs->js test-map#))))

(defmacro testing [test-map desc & body]
  `(assoc ~test-map ~desc
         (fn []
           ~@body)))
