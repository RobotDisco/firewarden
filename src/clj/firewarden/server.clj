(ns firewarden.server
  (:require [firewarden.employee :as emp]
            [firewarden.json :refer [->json]]))

(defonce employee-list (atom {}))

(defn json-response [status content]
  {:status 200
   :headers {"Content-Type" "application/json"}
   :body (-> content
             ->json)})

(def successful-json-response
  (partial json-response 200))

(defn fetch-employees! []
  (when-not (seq @employee-list)
    (reset! employee-list (emp/local-employees "Toronto")))
  @employee-list)

(defn reset-employeelist! []
  (reset! employee-list {}))

(defn handler [req]
  (successful-json-response (fetch-employees!)))
