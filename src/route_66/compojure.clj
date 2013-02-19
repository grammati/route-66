(ns route-66.compojure
  (:require [route-66.core :as r66]))

(defmacro defroutes [name & routes]
  `(def ~name (r66/routes ~@routes)))

(defn context [& _])

(defn GET [& _])
(defn PUT [& _])
(defn POST [& _])
(defn DELETE [& _])
(defn HEAD [& _])
(defn OPTIONS [& _])
(defn PATCH [& _])
(defn ANY [& _])
