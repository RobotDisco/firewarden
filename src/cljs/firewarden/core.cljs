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
  (go (let [response (<! (http/get "http://localhost:9500/whatever"))]
        (swap! app-state assoc :employees (:body response)))))

(defn reset-app-state! []
  (swap! app-state assoc :__figwheel_counter 0)
  fetch-employee-list!)

(defn get-app-element []
  (gdom/getElement "app"))

(defn hello-world []
  [:div
   [:h4 (str @app-state)]])

(defn mount [elem]
  (reagent/render-component [hello-world] elem))

(defn mount-app-element []
  (when-let [elem (get-app-element)]
    (mount elem)))

;; conditionally start your applucation based on the presence of an "app" element
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
