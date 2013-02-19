(ns route-66.core
  (:require [clout.core :as clout]
            [compojure.response :as response]))


(defn compile-routes [routes]
  (mapv (fn [r] (assoc r :compiled-route (clout/route-compile (:path r))))
        routes))

(defn route-matches [request route]
  (if-let [route-params (clout/route-matches (:compiled-route route) request)]
    (-> request
        (merge route-params)
        (assoc :handler (:handler route)))))

(defn route-matcher
  "Returns a function thats checks a request against each of the given
  routes, and return a map of the route-params for the first match (or
  nil, if none match)."
  [routes]
  (let [routes (compile-routes routes)]
    (fn [request]
      (some (partial route-matches request) routes))))

(defn wrap-routing [handler routes]
  (let [matcher (route-matcher routes)]
    (fn [request]
      (handler (matcher request)))))

(defn handler [request]
  (if-let [handler (:handler request)]
    (response/render (handler request) request)))
