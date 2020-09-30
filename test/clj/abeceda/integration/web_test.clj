(ns ^:integration abeceda.integration.web-test
  (:require [abeceda.integration.helper :refer :all]
            [clojure.test :refer :all]))

(deftest serving-non-existent-resource-test
  (is (= 404 (:status (GET "/foo" {:throw-exceptions false})))))

(deftest can-serve-html-test
  (is (= [200
          "text/html"
          true]
         ((juxt :status
                (comp :content-type :headers)
                (comp pos? count :body))
          (GET "")))))

(deftest can-serve-static-files-test
  (is (= [200
          "text/css"
          true]
         ((juxt :status
                (comp :content-type :headers)
                (comp pos? count :body))
          (GET "/css/screen.css")))))

