(ns route-66.core
  (:require [clout.core :as clout]))


(defn routes [& args]
  (mapv (fn [r] (assoc r :compiled-route (clout/route-compile (:path r))))
        args))

(defn route-matcher
  "Returns a function thats checks a request against each of the given
  routes, and return a map of the route-params for the first match (or
  nil, if none match)."
  [routes]
  (fn [request]
    (some route-match routes)))

(defn wrap-routing [handler routes]
  (let [matcher (route-matcher routes)]
    (fn [request]
      (handler (merge request (matcher request))))))

(defn handler [request]
  )
