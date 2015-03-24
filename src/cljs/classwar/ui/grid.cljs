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

(ns classwar.ui.grid
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [classwar.engine :as engine]
            [classwar.world :as world]))

(defn rgb-str [v]
  (let [fascists-rgb (int (* 255 (:fascists v)))]
    (str "rgb(0, 0, " fascists-rgb ")")))

(defn render-grid [ctx state]
  (let [w (vec (range (:width state)))
        h (vec (range (:height state)))
        cell-size (:cell-size state)]
    (doseq [x w
            y h]
      (let [val (world/get-cell state x y)]
        (set! (. ctx -fillStyle) (rgb-str val))
        (.fillRect ctx (* x cell-size) (* y cell-size) cell-size cell-size))))
  state)

(defn get-render-context [owner canvas-id]
  (let [canvas (om/get-node owner canvas-id)]
    (.getContext canvas "2d")))

(defn get-cell [state x y]
  (let [width (-> state :map :width)
        idx (+ x (* y width))]
    (nth (-> state :map :cells ) idx)))


(defn canvas-view [game owner]
  (reify
    om/IWillMount
    (will-mount [this]
      (.log js/console "in willmount"))
    om/IDidMount
    (did-mount [_]
      (.log js/console "in did-mount")
      (let [ctx (get-render-context owner "game-canvas")]
        (render-grid ctx game)))
    om/IRenderState
    (render-state [this _]
      (.log js/console "in renderstate")
      (dom/canvas #js { :width 640 :height 640 :ref "game-canvas" } nil))))

(om/root canvas-view engine/game
         {:target (. js/document (getElementById "canvas-wrapper"))})
