(ns route-66.test.core
  (:require [clojure.test :refer :all]
            [route-66.core :as r66]
            [ring.mock.request :as mock]))



(deftest test-routing
  (testing "Basic route-matching"
    (let [routes  [{:path "/foo" :handler (constantly "foo")}
                   {:path "/bar" :handler (constantly "bar")}]
          matcher (r66/route-matcher routes)]
      (are [a b] (= a (->> b
                           (mock/request :get)
                           matcher
                           r66/handler
                           :body
                           ))
           "foo" "/foo"
           "bar" "/bar"
           nil   "/baz"))))


