ENV ?= "dev"

backend-repl:
	clojure -A:rebel --repl
frontend-repl:
	clojure -A:figwheel --build $(ENV) --repl
lint:
	clj -A:eastwood
