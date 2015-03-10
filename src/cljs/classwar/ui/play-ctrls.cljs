(ns classwar.ui.play-ctrls
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! put!]]
            [classwar.state :as state]
            [classwar.channels :as channels]))

(defonce ui-state (atom {:boons [
                                 {:n "one" :x 100 :y 20}
                                 {:n "two" :x 200 :y 200}]}))

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
  (.log js/console "in send start game" cmd-chan)
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
            (let [w (<! channels/event-chan)]
              (.log js/console "world: " w)
              (recur)))))

    om/IRender
    (render [this]
      (dom/div nil "World fu")))

  (defn play-ctrls-view [data owner]
    (.log js/console "data" data)
    (reify
      om/IRender
      (render [this]
        (dom/button #js { :onClick (partial send-start-game! channels/cmd-chan) } "Play")))))


(om/root day-label-view ui-state
         {:target (. js/document (getElementById "day-label"))})

(om/root play-ctrls-view ui-state
         {:target (. js/document (getElementById "play-ctrls"))})
