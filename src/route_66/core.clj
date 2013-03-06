(ns route-66.core
  (:require [clout.core :as clout]
            [compojure.response]))


(defn basic-handler
  "The simplest possible request handler.
   Just calls ((:handler request) request), or returns nil if there is
  no :handler."
  [request]
  (if-let [handler (:handler request)]
    (handler request)))

(defn wrap-response-conversion
  "Middleware that just calls compojure.response/render on the result
  of calling its wrapped handler."
  [handler]
  (fn [request]
    (compojure.response/render (handler request) request)))

(def default-handler
  "The default request-handler.
   Roughly equivalent to (compojure.response/render ((:handler request) request))."
  (-> basic-handler
      wrap-response-conversion))

(def handler default-handler)


(defn compile-routes [routes]
  (mapv (fn [r] (assoc r :compiled-route (clout/route-compile (:path r))))
        routes))

(defn method-matches [request route]
  (let [route-method  (:method route :get)
        reqest-method (:request-method request)]
    (or (= :any route-method)
        (= route-method reqest-method))))

(defn routed-request [request route]
  (if (method-matches request route)
    (if-let [route-params (clout/route-matches (:compiled-route route) request)]
      (-> request
          (assoc :matched-route route)
          (assoc :route-params route-params)
          (merge (select-keys route [:route-data :handler]))))))

(defn route-matcher
  "Returns a function thats checks a request against each of the given
  routes, and returns the request-map with information about the
  matched route (if any) merged in. If no route matches, the request
  map is returned unchanged."
  [routes]
  (let [routes (compile-routes routes)]
    (fn [request]
      (or (some (partial routed-request request) routes)
          request))))

(defn wrap-routing
  "Middleware that inserts information about the matched route into
  the request."
  [handler routes]
  (let [matcher (route-matcher routes)]
    (fn [request]
      (let [request (matcher request)]
        (if (get-in request [:matched-route :short-circuit])
          (default-handler request)
          (handler request))))))


(defn routes [& route-defs]
  )