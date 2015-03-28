(ns classwar.ui.stats
  (:require [clojure.string :as str]
            [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! put!]]
            [classwar.engine :as engine]
            [classwar.world :as world]))

(def stats-img-css #js {:width "50px"
                        :height "50px"})

(defn stats-money-view [game]
  (dom/div #js {:className "stats-money"
                :style #js {:display "inline-flex"}}
           (dom/img #js {:src "/img/megaphone.svg"
                         :style stats-img-css})
           (dom/div nil (:money game))))

(defn stats-activists-view [game]
  (dom/div #js {:className "stats-activists"
                :style #js {:display "inline-flex"}}
           (dom/img #js {:src "/img/activist.svg"
                         :style stats-img-css})
           (dom/div nil (:activists game))))

(defn get-pos-from-cell [game cell]
  [100 100])

(defn stats-boon-view [game boon]
  (let [[x y] (get-pos-from-cell game (get-in boon [:pos]))]
    (dom/div #js {:onClick (world/collect-boons game boon)
                  :style #js {:position "absolute"
                              :top x
                              :left y
                              :border "1px solid #000"
                              :background "#fff"}}
             ;; created 70, :pos [2 0], :recruitable 1, :money 0}
             (str boon))))

(defn stats-boons-view [game]
  (let [stats-b-v (partial stats-boon-view game)]
    (dom/div #js {:className "stats-boons"
                  :style #js {:display "inline-flex"}}
             (apply dom/div nil
                    (map stats-b-v (:boons game))))))

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
               (stats-boons-view game)))))

(defn create-ui [game]
  (om/root stats-view game
           {:target (. js/document (getElementById "stats"))}))
