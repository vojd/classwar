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

(ns classwar.core
  (:require    [cljs.core.async :as async]
               [om.core :as om :include-macros true]
               [om.dom :as dom :include-macros true]
               [classwar.world :as world]
               [classwar.engine :as engine]
               [classwar.render :as render]
               [classwar.state :as state]
               [classwar.channels :as channels]
               [classwar.ui.play-ctrls :as fu])

  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn get-render-context [canvas-id]
  (let [canvas (.getElementById js/document canvas-id)]
    (.getContext canvas "2d")))

(defn get-cell [state x y]
  (let [width (-> state :map :width)
        idx (+ x (* y width))]
    (nth (-> state :map :cells ) idx)))

(defn main []
  (.log js/console "in main")
  (let [world (engine/init-engine-state channels/cmd-chan)
        render-fn (partial render/render (get-render-context "canvas"))]
    (engine/start-game world render-fn)))
