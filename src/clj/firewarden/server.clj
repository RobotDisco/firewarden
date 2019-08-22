(ns firewarden.server
  (:require [firewarden.employee :as emp]
            [firewarden.json :refer [->json]]))

(defonce employee-list (atom {}))

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
