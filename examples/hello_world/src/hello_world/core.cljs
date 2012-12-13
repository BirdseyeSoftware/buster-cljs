(ns hello-world.core)

(defn ^:export say-hello [language]
  (condp = language
   :english "Hello World"
   :french  "Bonjour tout le monde"
   :german  "Hallo Welt"
   :spanish "Hola Mundo"))
