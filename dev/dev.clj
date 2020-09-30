(ns dev
  "Utilities for developer convenience at the REPL."
  (:require [abeceda.specs]
            [abeceda.app :as app]
            [abeceda.api :as api]
            [abeceda.web :as web]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]
            [clojure.tools.logging :refer [error]]
            [criterium.core :as criterium]
            [integrant.repl :as ig-repl]
            [integrant.repl.state :as ig-repl-state])
  (:import (java.io File)))

(defn describe-api-scramble-impls
  "Print out some quick bench data"
  ([]
   (println "\n1e2")
   (describe-api-scramble-impls (int 1e2))
   (println "\n1e3")
   (describe-api-scramble-impls (int 1e3))
   (println "\n1e4")
   (describe-api-scramble-impls (int 1e4)))
  ([n]
   (let [super-samples (gen/sample (s/gen :api/az-str) n)
         sub-samples (gen/sample (s/gen :api/az-str) n)
         pairs (mapv vector super-samples sub-samples)]
     (doall
       (for [impl [api/scramble-freq-set-impl
                   api/scramble-coll-loop-impl
                   api/scramble-freq-reduce-impl
                   api/scramble-group-by-reduce-kv-impl
                   api/scramble-fold-impl]]
         (binding [api/*scramble-impl* impl]
           (println (str "--- " (type impl)))
           (criterium/quick-bench
             (doall (map (partial apply api/scramble?) pairs)))))))))

(ig-repl/set-prep!
  (fn []
    (merge (app/system-config)
           {::app/config {::web/live-reload? true
                          ::web/js-variant   "out"}})))

(defn ig-go
  "Spin up the system"
  []
  (Thread/setDefaultUncaughtExceptionHandler
    (reify Thread$UncaughtExceptionHandler
      (uncaughtException [_ t e]
        (error e "An error occurred on thread" t))))
  (ig-repl/go))

(defn ig-halt
  "Halt the system"
  []
  (ig-repl/halt))

(defn ig-reset
  []
  (ig-repl/reset))

(defn ig-system
  "System lookup "
  []
  ig-repl-state/system)

(defn logback-info
  "Figure out the logback config file"
  []
  (or (System/getProperty "logback.configurationFile") "logback.xml"))

(comment (defn run-tests
           []
           (->> "test/clj"
                File.
                file-seq
                (filter (fn [^File f]
                          (.isFile f)))
                (sort-by (fn [^File f]
                           (.getName f)))
                (mapv (fn [^File f]
                        (load-file (.getCanonicalPath f)))))
           (clojure.test/run-all-tests #"abeceda\.((?!browser).)+")))