(ns firewarden.json
  (:require [cheshire.core :as json]))

(def ->json
  json/generate-string)

(defn json->
  "Serialize ring `response` from JSON into clojure object"
  [response]
  (-> response
      :body
      (json/parse-string true)))
