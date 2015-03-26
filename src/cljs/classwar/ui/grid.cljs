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
            [cljs.core.async :refer [<! put!]]
            [classwar.engine :as engine]
            [classwar.world :as world]))


(defn rgb-str [v]
  (let [fascists-rgb (int (* 255 (:fascists v)))]
    (str "rgb(0, 0, " fascists-rgb ")")))

(defn render-grid [ctx game]
  (let [w (vec (range (:width game)))
        h (vec (range (:height game)))
        [cell-size-x cell-size-y] (get-cell-size (.-canvas ctx) (:width game) (:height game))]

    (doseq [x w
            y h]
      (let [val (world/get-cell game x y)]
        (set! (. ctx -fillStyle) (rgb-str val))
        (.fillRect ctx (* x cell-size-x) (* y cell-size-y) cell-size-x cell-size-y))))
  game)

(defn get-render-context [owner canvas-id]
  (let [canvas (om/get-node owner canvas-id)]
    (.getContext canvas "2d")))

(defn get-cell-size [canvas w h]
  (let [canvas-width (.-width canvas)
        canvas-height (.-height canvas)
        grid-width w
        grid-height h]
    [(/ canvas-width grid-width)
     (/ canvas-height grid-height)]))

(defn get-cell [w h canvas [x y]]
  (let [canvas-width (.-width canvas)
        canvas-height (.-height canvas)
        grid-width w
        grid-height h
        [cell-size-x cell-size-y] (get-cell-size canvas w h)
        rx (/ x cell-size-x)
        ry (/ y cell-size-y)]

    [(.floor js/Math rx )
     (.floor js/Math ry)]))

(defn get-click-pos [click-event]
  (let [canvas (aget click-event "target")
        x (aget click-event "clientX")
        y (aget click-event "clientY")
        bounding-rect (.getBoundingClientRect canvas)]
    [(- x (.-left bounding-rect))
     (- y (.-top bounding-rect))]))


(defn send-start-antifa-op! [cmd-chan x y]
  ;; This is just for debugging - should be hooked up to ui
  (put! cmd-chan {:msg-id :start-op
                        :op world/antifa-flyers
                        :pos [x y]}))

(defn canvas-on-click [w h click-event]
  (let [pos (get-click-pos click-event)
        canvas (aget click-event "target")
        [x y] (get-cell w h canvas pos)]
    (send-start-antifa-op! engine/cmd-chan x y)))

(defn canvas-view [game owner]
  (reify
    om/IInitState
    (init-state [_]
      {:game game})
    om/IWillMount
    (will-mount [this]
      (.log js/console "in willmount"))
    om/IDidMount
    (did-mount [_]
      (.log js/console "in did-mount")
      (let [ctx (get-render-context owner "game-canvas")]
        (render-grid ctx game)))

    om/IDidUpdate
    (did-update [_ _ _]
      (.log js/console "in did-update")
      (let [ctx (get-render-context owner "game-canvas")]
        (render-grid ctx game)))

    om/IRenderState
    (render-state [this state]
      (dom/canvas #js {
                       :width 640
                       :height 640
                       :ref "game-canvas"
                       :onClick (partial canvas-on-click (:width game) (:height game))
                       } nil))))

(defn create-ui [game]
  (om/root canvas-view game
           {:target (. js/document (getElementById "canvas-wrapper"))}))
