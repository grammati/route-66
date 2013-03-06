(ns route-66.core-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [route-66.core :as r66]))


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
           nil   "/baz")))

  (testing "Matching by request method"
    (let [routes (conj (for [meth [:get :put :post :delete :head]]
                         {:path "/foo"
                          :method meth
                          :handler (constantly ["foo" meth])})
                       {:path "/bar"
                        :method :get
                        :handler (constantly "bar")})
          matcher (r66/route-matcher routes)]
      (are [a b m] (= a (->> b
                             (mock/request m)
                             matcher
                             r66/basic-handler
                             ))
           "bar"          "/bar" :get
           nil            "/bar" :put
           nil            "/bar" :post
           nil            "/bar" :delete
           ;["foo" :get]   "/foo" :get
           )))

  (testing "Parameterized matching"
    (let [routes [{:path "/foo/:id"
                   :handler #(get-in % [:route-params :id])}]
          matcher (r66/route-matcher routes)]
      (are [a b] (= a (->> b
                           (mock/request :get)
                           matcher
                           r66/handler
                           :body))
           "123" "/foo/123")))

  (testing "Extra per-route data"
    (let [routes [{:path       "/foo"
                   :handler    #(get-in % [:route-data :bar])
                   :route-data {:bar "bar"}}]
          matcher (r66/route-matcher routes)]
      (are [a b] (= a (->> b
                           (mock/request :get)
                           matcher
                           r66/handler
                           :body))
           "bar" "/foo"
           nil   "/abc")))

  (testing "Short-circuiting handlers"
    (let [routes       [{:path          "/foo"
                         :handler       (constantly "foo")
                         :short-circuit true}]
          wrap-blow-up (fn [h] (fn [req] (throw (Exception.))))
          handler      (-> r66/handler
                           wrap-blow-up
                           (r66/wrap-routing routes))]
      (is (= "foo" (->> "/foo"
                        (mock/request :get)
                        handler
                        :body)))
      (is (thrown? Exception (->> "/bar"
                                  (mock/request :get)
                                  handler)))))

  (testing "Nested routes"
    (let [routes [{:path "/foo"
                   :routes [{:path "/bar"
                             :handler (constantly "foo-bar")}]}]
          matcher (r66/route-matcher routes)]
      (are [a b] (= a (->> b
                           (mock/request :get)
                           matcher
                           r66/handler
                           :body))
           "foo-bar" "/foo/bar")))

  )
