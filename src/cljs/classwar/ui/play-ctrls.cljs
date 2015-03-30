;; Copyright (C) 2015
;; Anders Sundman <anders@4zm.org>
;; Mathias Tervo <mathias.tervo@gmail.com>
;;
;; This program is free software: you can redistribute it and/or modify
;; it under the terms of the GNU General Public License as published by
;; the Free Software Foundation, either version 3 of the License, or
;; (at your option) any later version.
;;
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; GNU General Public License for more details.
;;
;; You should have received a copy of the GNU General Public License
;; along with this program.  If not, see <http://www.gnu.org/licenses/>.

(ns classwar.ui.play-ctrls
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! put!]]
            [classwar.engine :as engine]
            [classwar.world :as world]))

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

(defn create-ui [game]
  (om/root day-label-view game
           {:target (. js/document (getElementById "day-label"))})

  (om/root play-ctrls-view game
           {:target (. js/document (getElementById "play-ctrls"))}))
