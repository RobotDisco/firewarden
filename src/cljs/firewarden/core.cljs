(ns ^:figwheel-hooks firewarden.core
  (:require
   [firewarden.component :as fwc]
   [firewarden.state :as fws]
   [goog.dom :as gdom]
   [reagent.core :as reagent]))

;; Reagent bootstrapping
(defn get-app-element []
  (gdom/getElement "app"))

(defn mount [elem]
  (reagent/render-component [fwc/app] elem))

(defn mount-app-element []
  (when-let [elem (get-app-element)]
    (mount elem)))

(defn initialize-app!
  "conditionally start your application based on the presence of an app element
  this is particularly helpful for testing this ns without launching the app."
  []
  (when-not (seq (:employees @fws/app-state))
    (fws/fetch-employee-list!))
  (mount-app-element))

;; specify reload-hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (initialize-app!))

(initialize-app!)


