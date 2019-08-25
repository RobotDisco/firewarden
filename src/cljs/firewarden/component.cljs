(ns firewarden.component
  (:require
   [firewarden.state :as fws]))

(defn employee-info
  "Component for each row in our employee list."
  [{:keys [id photoUrl firstName lastName in-office? accounted-for?]}]
  [(cond (not in-office?) :tr.inactive
         accounted-for? :tr.accounted-for
         :else :tr)
   (when in-office?
     {:on-mouse-down (fws/gen-account-for! id)})
   [:td [(if in-office? :img.profile :img.profile.grayscale) {:src photoUrl}]]
   [:td firstName " " lastName]])

(defn employee-list
  "List view for our employees"
  [employees]
  [:table
   [:tbody
    (for [employee employees]
      ^{:key (:id employee)} [employee-info employee])]])

(defn app
  "Root smart container/component for firewarden app."
  []
  [:div
   [employee-list (sort-by :in-office? (sort-by :lastName (vals (:employees @fws/app-state))))]])
