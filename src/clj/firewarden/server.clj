(ns firewarden.server)

(defn handler [req]
  {:status 200
   :headers {"Content-Type" "text/plain"}
   :body "Hello there."})
