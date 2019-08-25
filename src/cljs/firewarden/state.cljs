(ns ^:figwheel-hooks firewarden.state
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:require
   [cljs.core.async :refer [<!]]
   [cljs-http.client :as http]
   [reagent.core :refer [atom]]))


(defonce app-state
  (atom {:__figwheel_counter 0, :employees []}))

(defn fetch-employee-list!
  "Fetch employee list + OOO status from BambooHR"
  []
  (go (let [response (<! (http/get "http://localhost:9500/whatever"))
            employees (:body response)
            employees-accounted (map #(assoc % :accounted-for? false) employees)
            emp-map (zipmap (map :id employees-accounted) employees-accounted)]
        (swap! app-state assoc :employees emp-map))))

(defn gen-account-for!
  "Generate browser event for `emp-id` to employee as accounted for."
  [emp-id]
  (fn [e]
    (swap! app-state update-in [:employees emp-id :accounted-for?] not)))

;; Develeopment helper functions
(defn reset-app-state! []
  (swap! app-state assoc :__figwheel_counter 0)
  fetch-employee-list!)

;; specify reload-hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (swap! app-state update :__figwheel_counter inc))


