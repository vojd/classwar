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
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [classwar.simulation :as sim]))

(defn day-label-view [data owner]
  (reify
    om/IRenderState
    (render-state [this _]
      (dom/div nil (:time data)))))

(defmulti play-ctrl :state)
(defmethod play-ctrl :new [g]
  {:caption "Play"
   :ctrl-op #(om/transact! g sim/start)})
(defmethod play-ctrl :running [g]
  {:caption "Pause"
   :ctrl-op #(om/transact! g sim/pause)})
(defmethod play-ctrl :paused [g]
  {:caption "Resume"
   :ctrl-op #(om/transact! g sim/resume)})

(defn play-ctrls-view [game owner]
  (reify
    om/IRender
    (render [this]
      (let [{:keys [caption ctrl-op]} (play-ctrl game)]
        (dom/button
         #js { :onClick #(ctrl-op game) }
         caption)))))

(defn create-ui [game]
  (om/root day-label-view game
           {:target (. js/document (getElementById "day-label"))})

  (om/root play-ctrls-view game
           {:target (. js/document (getElementById "play-ctrls"))}))
