(ns abeceda.api
  (:require [abeceda.specs]
            [clojure.spec.alpha :as s]
            [clojure.core.reducers :as r]
            [integrant.core :as ig]
            [compojure.core :as compojure]
            [ring.middleware.json :as ring-json]
            [ring.util.response :as response]))

(def ^:dynamic *scramble-impl* nil)

(defn scramble?
  "The function that returns true if a portion of str1 characters can be rearranged to match str2,
   otherwise returns false"
  [str1 str2]
  {:pre [(s/valid? :api/az-str str1)
         (s/valid? :api/az-str str2)]}
  (if
    (< (count str1) (count str2))
    false
    (*scramble-impl* str1 str2)))

(defn scramble-freq-set-impl
  [str1 str2]
  (let [f1 (frequencies str1)
        f2 (frequencies str2)]
    (and
      (clojure.set/subset?
        (set (keys f2))
        (set (keys f1)))
      (every? (fn [k]
                (<= (get f2 k 0) (get f1 k 0)))
              (keys f2)))))

(defn scramble-coll-loop-impl
  [str1 str2]
  (letfn [(while-pred [x]
            (complement #{x}))
          (second-rest-into-first [[h t]]
            (into h (rest t)))
          (remove-single [coll item]
            (second-rest-into-first (split-with (while-pred item) coll)))]
    (loop [s1 (seq str1)
           s2 (seq str2)]
      (let [super (rest s1)
            sub (remove-single s2 (first s1))]
        (cond (empty? sub) true
              (empty? super) false
              :else (recur super sub))))))

(defn scramble-freq-reduce-impl
  [str1 str2]
  (:doable?
    (reduce
      (fn [{:keys [doable?] :as m} c]
        (if
          doable?
          (let [f (get m c 0)]
            (assoc! m c (dec f) :doable? (pos? f)))
          m))
      (assoc! (transient (frequencies str1)) :doable? true)
      str2)))

(defn scramble-group-by-reduce-kv-impl
  [str1 str2]
  (let [m (group-by identity str1)]
    (->> (group-by identity str2)
         (reduce-kv (fn [doable? k v]
                      (when doable?
                        (<= (count v) (count (get m k)))))
                    true)
         boolean)))

(defn scramble-fold-impl
  [str1 str2]
  (letfn
    [(combine-f
       ([] {})
       ([m1 m2] (merge-with + m1 m2)))
     (inc-op [x]
       ((fnil inc 0) x))
     (dec-op [x]
       ((fnil dec 0) x))
     (do-ops [op s]
       (r/fold
         ;; forcing here chunk size lower than default=512 makes things worse (in terms of time cost)
         combine-f
         (fn [m c]
           (update m c op))
         s))]
    (every?
      (comp not neg? second)
      (combine-f
        (do-ops inc-op (seq str1))
        (do-ops dec-op (seq str2))))))

(defmethod ig/init-key ::routes
  [_ _]
  [(-> (compojure/POST
         "/api/scramble"
         [str1 str2]
         (try
           (response/response
             {:scramble?
              (binding [*scramble-impl* scramble-freq-set-impl]
                (scramble? str1 str2))})
           (catch AssertionError _
             {:status 400})))
       ring-json/wrap-json-params
       ring-json/wrap-json-response)])