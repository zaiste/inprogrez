(ns inprogrez.views.welcome
  (:require [inprogrez.views.common :as common])
  (:use [noir.core :only [defpage]]
        hiccup.core)
  (:use somnium.congomongo)
  (:use [somnium.congomongo.config :only [*mongo-config*]]))

(defn split-mongo-url [url]
  "Parses MongoDB URL" 
  (let [matcher (re-matcher #"^.*://(.*?):(.*?)@(.*?):(\d+)/(.*)$" url)]
    (when (.find matcher)
      (zipmap [:match :user :pass :host :port :db] (re-groups matcher)))))

(defn maybe-init []
  "Initialize connection & collection"
  (when (not (connection? *mongo-config*))
    (let [mongo-url (get (System/getenv) "MONGOHQ_URL")
          config    (split-mongo-url mongo-url)]
      (mongo! :db (:db config) :host (:host config) :port (Integer. (:port config)))
      (authenticate (:user config) (:pass config))
      (or (collection-exists? :counter-coll) (create-collection! :counter-coll)))))

(defpage "/welcome" []
         (maybe-init)
         (let [counter (fetch-and-modify :counter-coll
                                         {:_id "counter"}
                                         {:$inc {:value 1}}
                                         :return-new true :upsert? true)]
           (common/layout
             [:p (str "Welcome to inprogrez" (or (:value counter) 0))])))
