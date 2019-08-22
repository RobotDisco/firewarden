(ns firewarden.server
  (:require [cheshire.core :as json]
            [firewarden.employee :as emp]))

(defonce employee-list (atom {}))

(def ->json
  json/generate-string)

(defn fetch-employees! []
  (when-not (seq @employee-list)
    (reset! employee-list (emp/local-employees "Toronto")))
  (-> @employee-list
      ->json))

(defn reset-employeelist! []
  (reset! employee-list {}))

(defn handler [req]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (fetch-employees!)})
