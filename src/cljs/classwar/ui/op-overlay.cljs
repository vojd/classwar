(ns classwar.ui.op-overlay
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            ))

(defn operation-view [game grid-to-px-fn operation]
  (let [[gx gy :as grid-pos] (:pos operation)
        [x y] (grid-to-px-fn grid-pos)
        progress (* 100 (/  (- (:time game) (:start operation)) (:duration operation)))]
    (.log js/console "pr" progress)
    (.log js/console (str progress "%"))
    (dom/div #js {:className "operation-overlay"
                  :style #js {:top x
                              :left y
                              :background "url('img/activist.svg')"}}
             (dom/div #js {:className "operation-overlay-progress-bar"}
                      (dom/div #js {:className "operation-overlay-progress-bar-overlay"
                                    :style #js {:width (str progress "%")}})))))

(defn operations-view [game owner]
  (reify om/IRenderState
    (render-state [this {:keys [grid-to-px-fn]}]
      (apply dom/div nil (map #(operation-view game grid-to-px-fn %) (:operations game))))))
