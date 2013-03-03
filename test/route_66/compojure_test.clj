(ns route-66.compojure-test
  (:require [clojure.test :refer :all]
            [route-66.compojure :refer [defroutes context GET PUT POST DELETE]]
            [route-66.core :as r66]
            [ring.mock.request :as mock]))


;;; Compojure compatibility - defroutes defines a handler that can be
;;; used exactly like a normal compojure routes definition.

(defn echo [data]
  (fn [request]
    {:status 200 :body (assoc request :data data)}))

(defroutes test-routes-1
  (GET "/" [] (echo "GET /"))
  (context "/foo"
           (GET "/bar" [] (echo "GET /foo/bar"))))

#_(deftest test-compojure-compat
  (is (= "hello" (-> (mock/request :get "/")
                     test-routes-1
                     :body
                     :data))))
