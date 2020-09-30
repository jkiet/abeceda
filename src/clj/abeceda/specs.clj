(ns abeceda.specs
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(def az-set
  (->> (range (int \a) (inc (int \z)))
       (map char)
       set))

(s/def :api/az-str
  (s/with-gen
    (s/and
      string?
      (complement empty?)
      (s/conformer seq)
      (s/* az-set))
    #(gen/fmap
       (partial apply str)
       (s/gen (s/+ az-set)))))
