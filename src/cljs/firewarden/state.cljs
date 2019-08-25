(ns ^:figwheel-hooks firewarden.state
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:require
   [cljs.core.async :refer [<!]]
   [cljs-http.client :as http]
   [reagent.core :refer [atom]]))

;; application state
(defonce app-state (atom {:__figwheel_counter 0, :employees []}))

;; Actions/Events ... things that change app date
(defn fetch-employee-list! []
  (go (let [response (<! (http/get "http://localhost:9500/whatever"))
            employees (:body response)
            employees-accounted (map #(assoc % :accounted-for? false) employees)
            emp-map (zipmap (map :id employees-accounted) employees-accounted)]
        (swap! app-state assoc :employees emp-map))))

(defn account-for! [emp-id]
  (fn [e]
    (prn emp-id)
    (swap! app-state update-in [:employees emp-id :accounted-for?] not)))

;; Actions designed for working with state when developing
(defn reset-app-state! []
  (swap! app-state assoc :__figwheel_counter 0)
  fetch-employee-list!)

(defn ^:after-load on-reload []
  (swap! app-state update :__figwheel_counter inc))


