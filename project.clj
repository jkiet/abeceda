(defproject abeceda "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url  "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/tools.logging "1.1.0"]
                 [ch.qos.logback/logback-classic "1.2.3"]
                 [integrant "0.8.0"]
                 [compojure "1.6.2"]
                 [ring "1.8.1"]
                 [ring/ring-defaults "0.3.2"]
                 [ring/ring-json "0.5.0"]
                 [selmer "1.12.30"]
                 [clj-http "3.10.3"]

                 [org.clojure/clojurescript "1.10.764"]
                 [reagent "1.0.0-alpha2"]
                 [cljs-ajax "0.8.1"]

                 [figwheel-sidecar "0.5.20"]]
  :plugins [[lein-figwheel "0.5.20"]
            [lein-cljsbuild "1.1.8"]]
  :main ^:skip-aot abeceda.core
  :target-path "target/%s"
  :source-paths ["src/clj"]
  :test-paths ["test/clj"]
  :test-selectors {:default     (constantly true)
                   :quick       (complement :integration)
                   :integration :integration}
  :profiles {:dev     {:source-paths ["dev"]
                       :injections   [(require 'dev)]
                       :dependencies [[integrant/repl "0.3.2"]
                                      [criterium "0.4.6"]
                                      [org.clojure/test.check "1.1.0"]]}
             :uberjar {:aot        [abeceda.core]
                       :prep-tasks ["compile" ["cljsbuild" "once" "min"]]
                       :jvm-opts   ["-Dclojure.compiler.direct-linking=true"]}}
  :aliases {"explain" ["run" "-m" "dev/describe-api-scramble-impls"]}
  :clean-targets ^{:protect false} ["resources/public/js" "target"]
  :resource-paths ["resources" "target/cljsbuild"]
  :cljsbuild {:builds
              {:app-target {:source-paths ["src/cljs" "test/cljs"]
                            :figwheel     true
                            :compiler     {:output-dir    "resources/public/js/out"
                                           :asset-path    "/js/out"
                                           :source-map    true
                                           :optimizations :none
                                           :pretty-print  true

                                           :modules       {:app {:entries   #{abeceda.browser.app}
                                                                 :output-to "resources/public/js/out/app.js"}}}}
               :min        {:source-paths ["src/cljs"]
                            :compiler     {:output-dir    "resources/public/js/min"
                                           :asset-path    "/js/min"
                                           :optimizations :simple
                                           :modules       {:app {:entries   #{abeceda.browser.app}
                                                                 :output-to "resources/public/js/min/app.js"}}}}}})
