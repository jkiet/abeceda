(ns abeceda.integration.helper
  (:require [abeceda.app :as app]
            [abeceda.web :as web]
            [integrant.core :as ig]
            [clj-http.client :as http-client]))

(defonce system-ref
         (delay
           (let [config (merge (app/system-config)
                               {::app/config {::web/server-port 8081}})
                 app (ig/init config)
                 shutdown-hook (Thread.
                                 ^Runnable
                                 (fn []
                                   (ig/halt! app)
                                   (shutdown-agents)))]
             (.addShutdownHook
               (Runtime/getRuntime)
               shutdown-hook)
             app)))

(defn req
  [method path opts]
  (http-client/request
    (merge
      {:url    (str "http://localhost:"
                    (get-in @system-ref [::app/config ::web/server-port])
                    path)
       :method method}
      opts)))

(defn POST
  ([path] (POST path {}))
  ([path opts]
   (req :post path opts)))

(defn GET
  ([path] (GET path {}))
  ([path opts]
   (req :get path opts)))
