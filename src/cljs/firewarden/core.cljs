(ns ^:figwheel-hooks firewarden.core
  (:require-macros
   [cljs.core.async.macros :refer [go]])
  (:require
   [cljs.core.async :refer [<!]]
   [cljs-http.client :as http]
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]))

;; application state
(defonce app-state (atom {:__figwheel_counter 0, :employees []}))

(defn fetch-employee-list! []
  (go (let [response (<! (http/get "http://localhost:9500/whatever"))
            employees (:body response)
            employees-accounted (map #(assoc % :accounted-for? false) employees)
            emp-map (zipmap (map :id employees-accounted) employees-accounted)]
        (swap! app-state assoc :employees emp-map))))

(defn reset-app-state! []
  (swap! app-state assoc :__figwheel_counter 0)
  fetch-employee-list!)

;; Events
(defn account-for [emp-id]
  (fn [e]
    (prn emp-id)
    (swap! app-state update-in [:employees emp-id :accounted-for?] not)))

;; Widgets -- move somewhere else
(defn employee-info
  [{:keys [id photoUrl firstName lastName in-office? accounted-for?]}]
  [(cond (not in-office?) :tr.inactive
         accounted-for? :tr.accounted-for
         :else :tr)
   (when in-office?
     {:on-touch-start (account-for id)})
   [:td [(if in-office? :img.profile :img.profile.grayscale) {:src photoUrl}]]
   [:td firstName " " lastName]])

(defn employee-list [employees]
  [:table
   [:tbody
    (for [employee employees]
      ^{:key (:id employee)} [employee-info employee])]])

(defn firewarden-app []
  [:div
   [employee-list (sort-by :in-office? (sort-by :lastName (vals (:employees @app-state))))]])

;; Reagent bootstrapping

(defn get-app-element []
  (gdom/getElement "app"))

(defn mount [elem]
  (reagent/render-component [firewarden-app] elem))

(defn mount-app-element []
  (when-let [elem (get-app-element)]
    (mount elem)))

;; conditionally start your application based on the presence of an "app" element
;; this is particularly helpful for testing this ns without launching the app
(mount-app-element)
(when-not (seq (:employees @app-state))
  (fetch-employee-list!))

;; specify reload-hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  (swap! app-state update :__figwheel_counter inc))
