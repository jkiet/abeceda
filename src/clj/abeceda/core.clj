(ns abeceda.core
  (:gen-class))

(defn -main
  [& args]
  (require 'abeceda.app)
  (apply (ns-resolve 'abeceda.app 'start!) args))
