(ns firewarden.core
  (:require [clj-http.client :as http]
            [environ.core :refer [env]]))

(def directory-url "https://api.bamboohr.com/api/gateway.php/tulip/v1/employees/directory/")
(def out-of-office-url "https://api.bamboohr.com/api/gateway.php/tulip/v1/time_off/whos_out/")
(def token (env :bamboohr-api-key))

(defn fetch-employee-list []
  (http/get directory-url {:accept :json :basic-auth [token "x"]}))

(defn fetch-out-of-office []
  (http/get out-of-office-url {:accept :json :basic-auth [token "x"]}))
