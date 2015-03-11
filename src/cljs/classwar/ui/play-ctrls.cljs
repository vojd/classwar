(ns classwar.ui.play-ctrls
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! put!]]
            [classwar.state :as state]
            [classwar.ui.state :as ui-state]))

(defn send-start-antifa-op! [cmd-chan x y]
  ;; This is just for debugging - should be hooked up to ui
  (put! cmd-chan {:msg-id :start-op
                        :op state/antifa-flyers
                        :pos [x y]}))

(defn send-collect-boon! [cmd-chan x y]
  ;; This is just for debugging - should be hooked up to ui
  (put! cmd-chan {:msg-id :collect-boon
                        :pos [x y]}))

(defn send-start-game! [cmd-chan]
  (put! cmd-chan {:msg-id :start-game}))

(defn send-pause-game [cmd-chan]
  (put! cmd-chan {:msg-id :pause-game}))

(defn send-resume-game! [cmd-chan]
  (put! cmd-chan {:msg-id :resume-game}))

(defn day-label-view [data owner]

  (reify
    om/IWillMount
    (will-mount [_]
      (go (loop []
            (let [w (<! (:event-chan @ui-state/ui-state))]
              (om/transact! data :time
                            (fn [xs] (-> w :world :time)))
              (recur)))))
    om/IRenderState
    (render-state [this _]
      (dom/div nil (:time data)))))

 (defn play-ctrls-view [data owner]
    (reify
      om/IRender
      (render [this]
        (dom/button
         #js { :onClick (partial send-start-game! (:cmd-chan @ui-state/ui-state)) }
         "Play"))))


 (defn start-antifa-campaign-ctrl [data owner]
    (reify
      om/IRender
      (render [this]
        (dom/button
         #js {:onClick (partial send-start-antifa-op! (:cmd-chan @ui-state/ui-state) 0 0)}
         "Start antifa campaign"))))

(om/root day-label-view ui-state/ui-state
         {:target (. js/document (getElementById "day-label"))})

(om/root play-ctrls-view ui-state/ui-state
         {:target (. js/document (getElementById "play-ctrls"))})

(om/root start-antifa-campaign-ctrl ui-state/ui-state
         {:target (. js/document (getElementById "start-antifa-campaign"))})
