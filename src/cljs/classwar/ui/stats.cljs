(ns classwar.ui.stats
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]
            [classwar.state :as state]
            [classwar.core :as core]))

(defonce ui-state (atom {:boons [
                                 {:n "one" :x 100 :y 20}
                                 {:n "two" :x 200 :y 200}]}))

;; stats
(defn stats-view [data owner]
  (let [style #js {:background "#3030303"}]
    (reify
      om/IInitState
      (init-state [_]
        (.log js/console "initing")
        {:stuff core/cmd-chan})

      om/IWillMount
      (will-mount [_]
        (go (loop []
              (let [data (<! core/cmd-chan)]
                (.log js/console "stats-view will mount", data)
                (recur)))))

      om/IRenderState
      (render-state [this state]
        (dom/div nil (str "this is stats"))))))


(om/root stats-view []
         {:target (. js/document getElementById "stats")})


;; actions

;; boons
(defn boon-view [boon owner]
  (let [style #js {:left (:x boon)
                   :top (:y boon)
                   :position "absolute"}]
    (reify
      om/IRender
      (render [this]
        (dom/div #js {:className "boon" :style style} (str (:n boon) (:x boon) (:y boon)))))))

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
