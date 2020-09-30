(ns abeceda.web
  (:require [clojure.tools.logging :refer [info]]
            [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :as reload]
            [ring.middleware.defaults :as defaults]
            [ring.middleware.content-type :as content-type]
            [ring.util.response :as response]
            [compojure.core :as compojure]
            [compojure.route :as route]
            [selmer.parser :as selmer-parser])
  (:import (org.eclipse.jetty.server Server)))

(defmethod ig/init-key ::routes
  [_ {:keys [app-config]}]
  (let [{:keys [::cache-busting-tag
                ::js-variant]} app-config]
    [(compojure/GET
       "/"
       []
       (response/content-type
         (response/response
           (selmer-parser/render-file
             "public/index.html"
             {:cfg {:tag cache-busting-tag :variant js-variant}}))
         "text/html"))]))

(defmethod ig/init-key ::server
  ^Server
  [_ {:keys [app-config
             web-routes
             api-routes]}]
  (let [{:keys [::server-port
                ::live-reload?]} app-config]
    (info "Starting web server on port" server-port)
    (jetty/run-jetty
      (-> (apply
            compojure/routes
            (conj (into web-routes api-routes) (route/not-found "Not found")))
          (cond->
            live-reload? (reload/wrap-reload))
          (defaults/wrap-defaults {:static {:resources "public"}})
          ;; proper mime-type for static resources
          content-type/wrap-content-type)
      {:port  server-port
       :join? false})))

(defmethod ig/halt-key! ::server
  [_ ^Server svr]
  (info "Stopping web server")
  (.stop svr))