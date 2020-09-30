(ns abeceda.browser.app
  "The `app` module entry point."
  (:require [reagent.core :as r]
            [reagent.dom :as rdom]
            [ajax.core :refer [POST]]))

(enable-console-print!)

(def state->resp-resolve-fns
  {:initial {:css      (fn [_] "is-info")
             :ans      (fn [_]
                           "Test if a portion of str1 characters can be rearranged to match str2.
                            Only lower case letters will be used (a-z).")
             :on-hold? (constantly false)}
   :inquiry {:css      (fn [_] "is-info")
             :ans      (fn [_] "?")
             :on-hold? (constantly true)}
   :success {:css      (comp {true  "is-primary"
                              false "is-warning"
                              nil   "is-danger"} :scramble?)
             :ans      (comp {true  "true"
                              false "false"
                              nil   "error"} :scramble?)
             :on-hold? (constantly false)}
   :error   {:css      (fn [_] "is-danger")
             :ans      (fn [{:keys [status
                                    status-text]}]
                           (str "error"
                                (if
                                  (= 400 status)
                                  ": invalid input string (only [a-z] letters allowed)"
                                  (str (some->> status-text
                                                (str ": "))))))
             :on-hold? (constantly false)}})

(def app-state (r/atom {:state    :initial
                        :response nil
                        :str1     ""
                        :str2     ""}))

(defn resolve-key
  [k]
  (let [{:keys [state
                response]} @app-state
        resp-resolve-fns (state state->resp-resolve-fns)]
    ((k resp-resolve-fns) response)))

(defn view
  []
  [:div.section
   [:div.notification.has-text-centered
    {:class (resolve-key :css)}
    (resolve-key :ans)]

   [:div.field
    [:div.control
     [:input.input.is-large.is-primary
      {:placeholder "str1"
       :disabled    (resolve-key :on-hold?)
       :value       (:str1 @app-state)
       :on-change   (fn [e]
                        (swap! app-state assoc
                               :str1 (-> e .-target .-value)))}]]]
   [:div.field
    [:div.control
     [:input.input.is-large.is-primary
      {:placeholder "str2"
       :disabled    (resolve-key :on-hold?)
       :value       (:str2 @app-state)
       :on-change   (fn [e]
                        (swap! app-state assoc
                               :str2 (-> e .-target .-value)))}]]]
   [:div.field
    [:div.control
     [:button.button.is-large.is-info.is-fullwidth
      {:disabled (resolve-key :on-hold?)
       :on-click (fn [_]
                     (swap! app-state assoc
                            :state :inquiry
                            :response nil)
                     (POST
                       "/api/scramble"
                       {:params          {:str1 (:str1 @app-state)
                                          :str2 (:str2 @app-state)}
                        :format          :json
                        :response-format :json
                        :keywords?       true
                        :handler         (fn [r]
                                             (swap! app-state assoc
                                                    :state :success
                                                    :response r))
                        :error-handler   (fn [r]
                                             (swap! app-state assoc
                                                    :state :error
                                                    :response r))}))}
      "scramble?"]]]])

(defn init!
  []
  (rdom/render [view] (.getElementById js/document "app")))

(init!)