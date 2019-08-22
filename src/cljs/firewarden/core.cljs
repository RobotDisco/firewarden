(ns ^:figwheel-hooks firewarden.core
  (:require
   [goog.dom :as gdom]
   [reagent.core :as reagent :refer [atom]]))

;; application state
(defonce app-state (atom {:__figwheel_counter 0, :in-office [], :out-of-office []}))

(defn reset-app-state! []
  (reset! app-state {:__figwheel_counter 0, :in-office [], :out-of-office []}))

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

;; specify reload-hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element)
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  (swap! app-state update-in [:__figwheel_counter] inc))
