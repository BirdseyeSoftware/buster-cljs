(ns buster-cljs.assertions)

(defn cljs->js
  ;; stolen from
  ;; https://github.com/ibdknox/jayq/blob/master/src/jayq/util.cljs#L18
  "Recursively transforms ClojureScript maps into Javascript objects,
other ClojureScript colls into JavaScript arrays, and ClojureScript
keywords into JavaScript strings."
  [x]
  (cond
    (string? x) x
    (keyword? x) (name x)
    (map? x) (let [obj (js-obj)]
               (doseq [[k v] x]
                 (aset obj (cljs->js k) (cljs->js v)))
               obj)
    (coll? x) (apply array (map cljs->js x))
        :else x))

(defn is [bool & msg]
  (.assert js/buster bool msg))
