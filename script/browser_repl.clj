(use 'figwheel-sidecar.repl-api)

(start-figwheel!
  (figwheel-sidecar.config/fetch-config))

(cljs-repl)