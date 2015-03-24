(ns classwar.ui.stats
    (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! put!]]
            [classwar.engine :as engine]
            [classwar.world :as world]))

(defn stats-money-view [game]
  (dom/div nil
           (dom/div nil "Money: ")
           (dom/div nil (:money game))))

(defn stats-activists-view [game]
  (dom/div nil
           (dom/div nil "Activists: ")
           (dom/div nil (:activists game))))


(defn stats-view [game owner]
  (reify
    om/IDidUpdate
    (did-update [_ _ _]
      (.log js/console "stats-view update"))
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (stats-money-view game)
               (stats-activists-view game)
               ))))

(om/root stats-view engine/game
         {:target (. js/document (getElementById "stats"))})
