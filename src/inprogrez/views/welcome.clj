(ns inprogrez.views.welcome
  (:require [inprogrez.views.common :as common]
            [noir.content.getting-started])
  (:use [noir.core :only [defpage]]))

(defpage "/welcome" []
         (common/layout
           [:p "Welcome to inprogrez"]))
