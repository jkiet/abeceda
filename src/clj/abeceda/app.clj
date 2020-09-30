(ns abeceda.app
  (:require [abeceda.api :as api]
            [abeceda.web :as web]
            [integrant.core :as ig]
            [clojure.tools.logging :refer [info error]]))

(defmethod ig/init-key ::config
  [_ config]
  (merge {::web/server-port       8080
          ::web/live-reload?      false
          ::web/js-variant        "min"
          ::web/cache-busting-tag "2e7a"}
         config))

(defn system-config
  []
  {::config     {}
   ::api/routes {}
   ::web/routes {:app-config (ig/ref ::config)}
   ::web/server {:app-config (ig/ref ::config)
                 :web-routes (ig/ref ::web/routes)
                 :api-routes (ig/ref ::api/routes)}})

(defn start!
  "Standalone app entry point, intended to be called from `abeceda.core` namespace.
  At the REPL use dev/ig-* functions instead."
  [& args]
  (info "Logback config:" (or (System/getProperty "logback.configurationFile") "logback.xml"))
  (Thread/setDefaultUncaughtExceptionHandler
    (reify Thread$UncaughtExceptionHandler
      (uncaughtException [_ t e]
        (error e "An error occurred on thread" t))))
  (let [system (ig/init (system-config))
        lock (Object.)]
    (.addShutdownHook
      (Runtime/getRuntime)
      (Thread.
        ^Runnable
        (fn []
          (ig/halt! system)
          (shutdown-agents))))
    (locking lock
      (.wait lock))))