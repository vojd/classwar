(ns classwar.ui.play-ctrls
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! put!]]
            [classwar.engine :as engine]
            [classwar.world :as world]))

(defn send-start-antifa-op! [cmd-chan x y]
  ;; This is just for debugging - should be hooked up to ui
  (put! cmd-chan {:msg-id :start-op
                        :op world/antifa-flyers
                        :pos [x y]}))

(defn send-collect-boon! [cmd-chan x y]
  ;; This is just for debugging - should be hooked up to ui
  (put! cmd-chan {:msg-id :collect-boon
                        :pos [x y]}))

(defn send-start-game! [cmd-chan]
  (put! cmd-chan {:msg-id :start-game}))

(defn send-pause-game! [cmd-chan]
  (put! cmd-chan {:msg-id :pause-game}))

(defn send-resume-game! [cmd-chan]
  (put! cmd-chan {:msg-id :resume-game}))

(defn day-label-view [data owner]
  (reify
    om/IRenderState
    (render-state [this _]
      (dom/div nil (:time data)))))

(defmulti play-ctrl :state)
(defmethod play-ctrl :new [_]
  {:caption "Play" :ctrl-op send-start-game!})
(defmethod play-ctrl :running [_]
  {:caption "Pause" :ctrl-op send-pause-game!})
(defmethod play-ctrl :paused [_]
  {:caption "Resume" :ctrl-op send-resume-game!})

(defn play-ctrls-view [data owner]
  (reify
    om/IRender
    (render [this]
      (let [{:keys [caption ctrl-op]} (play-ctrl data)]
        (dom/button
         #js { :onClick (partial ctrl-op engine/cmd-chan) }
         caption)))))

 (defn start-antifa-campaign-ctrl [data owner]
    (reify
      om/IRender
      (render [this]
        (dom/button
         #js {:onClick (partial send-start-antifa-op! engine/cmd-chan 0 0)}
         "Start antifa campaign"))))

(defn create-ui [game]
  (om/root day-label-view game
           {:target (. js/document (getElementById "day-label"))})

  (om/root play-ctrls-view game
           {:target (. js/document (getElementById "play-ctrls"))})

  (om/root start-antifa-campaign-ctrl game
           {:target (. js/document (getElementById "start-antifa-campaign"))}))
