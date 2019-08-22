(ns firewarden.core
  (:require [cheshire.core :as json]
            [clj-http.client :as http]
            [java-time :as dt]
            [environ.core :refer [env]]))

(def directory-url
  "https://api.bamboohr.com/api/gateway.php/tulip/v1/employees/directory/")
(def out-of-office-url
  "https://api.bamboohr.com/api/gateway.php/tulip/v1/time_off/whos_out/")

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
