# route-66

A Clojure library for routing web requests.

I created Route-66 to solve some problems that I had with Compojure. In
particular, I wanted to separate the matching of routes from the handling of
requests. With route-66, routes are matched using Clout, as in Compojure, but
the handler is not immediately called; instead, it is inserted into the request
map under the `:handler` key to be handled later.

In addition to adding the `:handler` key, the route-matcher can insert other
data into the request-map, for use by middleware. With Compojure, this is not
possible because route-matching is always immediately followed by a call to the
handler - you cannot insert middleware that comes after route-matching.

Another goal of route-66 was to use significantly less macro-magic when defining
routes. Route-definitions are simply data - no macros invovled. This means that
you can use the full power of Clojure to build and transform your
route-definitions.

## Usage

```clojure
(ns my.awsome.webapp
  (:require [route-66 [core :as r66]
                      [compojure :refer :all]))

(defroutes app-routes
  (GET "/" [] "hello")
  (GET "/user/:id" [id] (show-user id))
  (context "/foo"
    (GET "/:id" [id] (show-foo id))
    (PUT "/" [] new-foo)))


(def handler
  (-> r66/handler                      ;; More-or-less just calls 
                                       ;; ((:handler request) request)

      some-middleware                  ;; This middleware can make decisions
                                       ;; that depend on which route was matched.

      (r66/wrap-routing app-routes)    ;; Matches the url, picks a handler, and
                                       ;; adds it to the request map. 

      other-middleware                 ;; Normal (pre-match) middleware
      ))


```


```clojure

(def app-routes
  [{:method :get :path "/" :handler (fn [] "hello")}
   {:method :get :path "/user/:id" :handler (fn [id] (show-user id))}
   {:method :any :path "/foo" :handler ???}])

```

## License

Copyright © 2013 Chris Perkins

Distributed under the MIT License.


