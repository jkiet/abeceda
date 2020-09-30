(ns ^:integration abeceda.integration.api-test
  (:require [abeceda.integration.helper :refer :all]
            [clojure.test :refer :all]))

(deftest api-scrample-bad-request-test
  (is (= 400
         (-> (POST "/api/scramble"
                   {:as               :json
                    :content-type     :json
                    :form-params      {}
                    :throw-exceptions false})
             :status)))
  (is (= 400
         (-> (POST "/api/scramble"
                   {:as               :json
                    :content-type     :json
                    :form-params      {:str1 "" :str2 ""}
                    :throw-exceptions false})
             :status))))

(deftest api-scramble-test
  (is (= [200
          true]
         ((juxt :status
                (comp :scramble? :body))
          (POST "/api/scramble"
                {:as           :json
                 :content-type :json
                 :form-params  {:str1 "a"
                                :str2 "a"}}))))
  (is (= [200
          true]
         ((juxt :status
                (comp :scramble? :body))
          (POST "/api/scramble"
                {:as           :json
                 :content-type :json
                 :form-params  {:str1 "fooo" :str2 "foo"}}))))
  (is (= [200
          false]
         ((juxt :status
                (comp :scramble? :body))
          (POST "/api/scramble"
                {:as           :json
                 :content-type :json
                 :form-params  {:str1 "foo" :str2 "off"}})))))