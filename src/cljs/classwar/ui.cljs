(ns classwar.ui
  (  :require [om.core :as om :include-macros true]
              [om.dom :as dom :include-macros true]

              [classwar.state :as state]))

(defonce ui-state (atom {:boons [
                                 {:n "one" :x 100 :y 20}
                                 {:n "two" :x 200 :y 200}]}))


(defn boon-view [boon owner]
  (let [style #js {:left (:x boon)
                   :top (:y boon)
                   :position "absolute"}]
    (reify
      om/IRender
      (render [this]
        (dom/div #js {:className "boon" :style style} (str (:n boon) (:x boon) (:y boon))))))  )

(defn boons-view [data owner]
  (.log js/console "data" data)
  (reify
    om/IRender
    (render [this]
      (dom/div nil
               (apply dom/div #js {:className "boons"}
                      (om/build-all boon-view (:boons data)))))))

(om/root boons-view ui-state
  {:target (. js/document (getElementById "app"))})
