(ns route-66.test.compojure
  (:require [route-66.compojure :refer [defroutes context GET PUT POST DELETE]]))



(comment
  (def zuul-routes
    [{:desc "Authenticated routes"
      :middleware [persistence/wrap-persistence-context
                   wrap-per-route-metrics]
      :routes
      [{:desc "Key routes"
        :path "key"
        :routes
        [{:desc "Key put (a.k.a. login)"
          :method :put
          :path #"^(|\.js)$"
          :handler key/create}
         {:desc "Key get (a.k.a. reauthenticate)"
          :method :get
          :path ":id(\.js)?"
          :middleware caching/wrap-get
          :handler key/read}
         {:desc "Key delete (a.k.a. logout)"
          :method :delete
          :path ":id(\.js)?"
          :middleware caching/wrap-delete
          :handler key/delete}]}
       {:desc "User routes"
        :path "user"
        :middleware auth/wrap-authentication
        :routes
        []}]}])


  (def zuul-routes
    (r66/routes
     (r66/routes "Authenticated routes"j
                 {:middleware [wrap-a wrap-b]})))

  (defroutes zuul-routes

    (with-middleware [persistence/wrap-persistence-context
                      wrap-per-route-metrics]

      (GET "/key/:id.js" [] (caching/wrap-get key/read))
      (PUT "/key.js" [] key/create)
      (DELETE "/key/:id.js" [] (caching/wrap-delete delete-key-handler))

      (GET "/user/:id.js" [] (auth/wrap-authentication user/get-by-id))
      (GET ["/user/name/:username" :username #".+"] [] (auth/wrap-authentication user/get-by-name))
      (PUT "/user.js" [] (auth/wrap-authentication user/create))
      (POST "/user/:id.js" [] (auth/wrap-authentication user/update))
      (DELETE "/user/:id.js" [] (auth/wrap-authentication user/delete))

      (GET "/subscription/:id.js" [] (auth/wrap-authentication subscription/get))
      (PUT "/subscription.js" [] (auth/wrap-authentication subscription/create))
      (POST "/subscription/:id.js" [] (auth/wrap-authentication subscription/update)))

    (DELETE "/subscription*" [] RequestHandlers/MethodNotAllowedHandler)

                                        ; New endpoints for metrics and healthcheck
    (GET "/status/metrics" [] metricsaurus/metrics-handler)
    (GET "/status/healthcheck" [] metricsaurus/healthcheck-handler)

    (ANY "/storage.js" [] tenant-handler)
    (ANY "/clean" [] tenant-handler)

    (route/files "/resources" {:root "server/resources/public"})
    (route/resources "/resources")

    (ANY "*" [] RequestHandlers/NotFoundHandler)))