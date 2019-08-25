(ns firewarden.employee
  (:require [clj-http.client :as http]
            [clojure.string :as string]
            [environ.core :refer [env]]
            [firewarden.json :refer [json->]]
            [java-time.local :as dt]))

(def bamboo-org
  (env :bamboohr-org-name))
(def scheme
  "https://")
(def hostname
  "api.bamboohr.com")
(def base-api-path
  "/api/gateway.php/")
(def api-version-path
  "/v1/")
(def base-url
  (string/join [scheme hostname base-api-path bamboo-org api-version-path]))

(def directory-url
  (string/join [base-url "employees/directory/"]))
(def out-of-office-url
  (string/join [base-url "time_off/whos_out/"]))

(def bamboo-hr-api-key
  (env :bamboohr-api-key))
(def basic-auth-credentials
  "BambooHR uses the API Key as the username, the password can be anything."
  [bamboo-hr-api-key "x"])

(def base-http-request-options
  "Basic options to pass `clj-http` when making HTTP requests
  We will always want responses back in JSON fpormat, and we will always have to
  supply the API token as part of BambooHR's basic auth requirements."
  {:accept :json
   :basic-auth basic-auth-credentials})

(defn today-str [] (str (dt/local-date)))

(defn fetch-employee-list
  "Call Employee List and return `clj-http` response object."
  []
  (http/get directory-url
            base-http-request-options))

(defn fetch-out-of-office
  "Call Out of Office List for today and return `clj-http` response object.
  We always want the list of people for today and today only."
  []
  (let [today (today-str)]
    (http/get
     out-of-office-url
     (merge base-http-request-options
            {:query-params {"start" today
                            "end" today}}))))

(defn out-of-office-employee-ids
  []
  (->> (fetch-out-of-office)
       json->
       ;; OOO returns numeric IDs but employee directory returns strings?!?!?
       ;; easiest to convert into a string here for consistency.
       (map (comp str :employeeId))))

(defn add-in-office-field-fn [ooo-seq]
  (let [in-office? (complement (set ooo-seq))]
    (fn [employee]
      (assoc employee
             :in-office?
             (-> employee :id in-office?)))))

(defn in-city? [city employee]
  (-> employee
      :location
      (= city)))

(defn local-employees
  "Fetch employee directory from BambooHR and return hashmap of employees from `city`.
  Returns a hash with two lists: the true key returns all employees who are marked as in-office, the false key contains all employees who are out of office."
  [city]
  (let [ooo-ids (out-of-office-employee-ids)
        add-in-office-field (add-in-office-field-fn ooo-ids)]
    (->> (fetch-employee-list)
         json->
         :employees
         (filter (partial in-city? city))
         (map #(select-keys % [:id :photoUrl :firstName :lastName]))
         (map add-in-office-field))))
