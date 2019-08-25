(ns ^:figwheel-hooks firewarden.core
  (:require
   [firewarden.state :as fws]
   [goog.dom :as gdom]
   [reagent.core :as reagent]))

;; Widgets -- move somewhere else
(defn employee-info
  [{:keys [id photoUrl firstName lastName in-office? accounted-for?]}]
  [(cond (not in-office?) :tr.inactive
         accounted-for? :tr.accounted-for
         :else :tr)
   (when in-office?
     {:on-mouse-down (fws/account-for id)})
   [:td [(if in-office? :img.profile :img.profile.grayscale) {:src photoUrl}]]
   [:td firstName " " lastName]])

(defn employee-list [employees]
  [:table
   [:tbody
    (for [employee employees]
      ^{:key (:id employee)} [employee-info employee])]])

(defn firewarden-app []
  [:div
   [employee-list (sort-by :in-office? (sort-by :lastName (vals (:employees @fws/app-state))))]])

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
(when-not (seq (:employees @fws/app-state))
  (fws/fetch-employee-list!))

;; specify reload-hook with ^:after-load metadata
(defn ^:after-load on-reload []
  (mount-app-element))
