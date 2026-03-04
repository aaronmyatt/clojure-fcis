(ns user
  "REPL entry point. Loads all modules for interactive development.

   Start a REPL with: bb nrepl
   Then this namespace is loaded automatically."
  (:require [fcis.core.user :as user]
            [fcis.core.schemas :as core-schemas]
            [fcis.adapter.user-store :as store]
            [fcis.app.main :as app]
            [fcis.schemas.common :as common]
            [malli.core :as m]
            [malli.dev :as dev]))

(defn start!
  "Initialize development environment.
   Call this after starting the REPL to enable Malli instrumentation."
  []
  (dev/start!)
  (println "FCIS dev environment started.")
  (println "Malli instrumentation enabled — schema violations will throw.")
  (println)
  (println "Available namespaces:")
  (println "  user/...         — this namespace (REPL helpers)")
  (println "  fcis.core.user   — pure user functions")
  (println "  fcis.adapter.user-store — user persistence")
  (println "  fcis.app.main    — application wiring")
  :ready)

;; ---- REPL Examples ----
(comment
  ;; Start dev environment
  (start!)

  ;; Try core functions
  (user/validate-email "alice@example.com")
  ;; => {:valid? true, :reason nil}

  (user/create-user "Alice" "alice@example.com")
  ;; => {:id "..." :name "Alice" :email "alice@example.com" :created-at ...}

  ;; Try adapter functions
  (store/save-user! "/tmp/fcis-dev" (user/create-user "Bob" "bob@example.com"))

  (store/list-users "/tmp/fcis-dev")

  ;; Try app functions
  (app/register-user! {:store-dir "/tmp/fcis-dev"
                        :name "Charlie"
                        :email "charlie@example.com"})
  )
