(ns abeceda.browser.app-test
  "Initial test for the REPL.
  The result should appear in the browser console."
  (:require [cljs.test :refer-macros [deftest is run-tests]]
            [abeceda.browser.app :as app]))

(deftest app-states-test
  (let [initial-state @app/app-state]
    (is (= :initial (:state @app/app-state)))
    (is (= "is-info" (app/resolve-key :css)))
    (is (string? (app/resolve-key :ans)))
    (is (not (app/resolve-key :on-hold?)))

    (swap! app/app-state assoc :state :inquiry)
    (is (= "is-info" (app/resolve-key :css)))
    (is (= "?" (app/resolve-key :ans)))
    (is (app/resolve-key :on-hold?))

    (swap! app/app-state assoc :state :success :response {:scramble? true})
    (is (= "is-primary" (app/resolve-key :css)))
    (is (= "true" (app/resolve-key :ans)))
    (is (not (app/resolve-key :on-hold?)))

    (swap! app/app-state assoc :state :success :response {:scramble? false})
    (is (= "is-warning" (app/resolve-key :css)))
    (is (= "false" (app/resolve-key :ans)))
    (is (not (app/resolve-key :on-hold?)))

    (swap! app/app-state assoc :state :success :response nil)
    (is (= "is-danger" (app/resolve-key :css)))
    (is (= "error" (app/resolve-key :ans)))
    (is (not (app/resolve-key :on-hold?)))

    (swap! app/app-state assoc :state :error :response {:status      400
                                                        :status-text "Bad request"})
    (is (= "is-danger" (app/resolve-key :css)))
    (is (= "error: invalid input string (only [a-z] letters allowed)" (app/resolve-key :ans)))
    (is (not (app/resolve-key :on-hold?)))

    (swap! app/app-state assoc :state :error :response {:status      418
                                                        :status-text "I'm a teapot"})
    (is (= "is-danger" (app/resolve-key :css)))
    (is (= "error: I'm a teapot" (app/resolve-key :ans)))
    (is (not (app/resolve-key :on-hold?)))

    (swap! app/app-state assoc :state :error :response nil)
    (is (= "is-danger" (app/resolve-key :css)))
    (is (= "error" (app/resolve-key :ans)))
    (is (not (app/resolve-key :on-hold?)))

    (reset! app/app-state initial-state)))

(run-tests 'abeceda.browser.app-test)