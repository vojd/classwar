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
            [cljs.core.async :refer [<! chan]]
            [classwar.world :as world]
            [classwar.simulation :as sim]
            [classwar.ui.op-menu :as op-menu]
            [classwar.ui.cell-stats :as cell-stats]
            [classwar.ui.boon :as boon-ui]
            [classwar.ui.op-overlay :as op-overlay-ui]
            )
  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn rgb-str [v]
  (let [fascists-rgb (int (* 255 (:fascists v)))]
    (str "rgb(0, 0, " fascists-rgb ")")))

(defn get-canvas-dim [owner canvas-id]
  (let [canvas (om/get-node owner canvas-id)
        bounding-rect (.getBoundingClientRect canvas)]
    {:top (.-top bounding-rect)
     :left (.-left bounding-rect)
     :width (.-width bounding-rect)
     :height (.-height bounding-rect)}))

(defn get-render-context [owner canvas-id]
  (let [canvas (om/get-node owner canvas-id)]
    (.getContext canvas "2d")))

(defn get-cell-size [canvas cols rows]
  (let [canvas-width (.-width canvas)
        canvas-height (.-height canvas)
        grid-width cols
        grid-height rows]
    [(/ canvas-width grid-width)
     (/ canvas-height grid-height)]))

(defn get-cell [cols rows canvas [x y]]
  (let [canvas-width (.-width canvas)
        canvas-height (.-height canvas)
        [cell-size-x cell-size-y] (get-cell-size canvas cols rows)
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


(defn render-grid [ctx game owner]
  (let [row (vec (range (:width game)))
        col (vec (range (:height game)))
        [cell-size-x cell-size-y] (get-cell-size (.-canvas ctx) (:width game) (:height game))]

    (doseq [x row
            y col]
      (let [val (world/get-cell game x y)]

        (set! (. ctx -fillStyle) (rgb-str val))
        (.fillRect ctx (* x cell-size-x) (* y cell-size-y) cell-size-x cell-size-y)
        (if (= [x y] (om/get-state owner :active-cell))
          (do
            (set! (. ctx -strokeStyle) "#0f0")
            (.strokeRect ctx (* x cell-size-x) (* y cell-size-y) cell-size-x cell-size-y)))

)))
  game)

(defn canvas-on-click [{w :width h :height :as game} owner click-event]
  "Toggle active cell"
  (let [pos (get-click-pos click-event)
        canvas (aget click-event "target")
        [x y] (get-cell w h canvas pos)
        active-cell (if (= (om/get-state owner :active-cell) [x y])
                       nil
                       [x y])]
    (om/set-state! owner :active-cell active-cell)

    ;; truthy if we should show the menu or not
    (om/set-state! owner :menu active-cell)))

(defn- get-pos-from-cell [owner game [x y]]
  (let [{:keys [top left width height]} (get-canvas-dim owner "game-canvas")]
       [(+ top (* y (/ height (:height game))))
        (+ left (* x (/ width (:width game))))]))

(defn canvas-view [game owner]
  (reify
    om/IInitState
    (init-state [_]
      {:menu nil
       :active-cell nil
       :launch (chan)})

    om/IWillMount
    (will-mount [this]
      (let [launch (om/get-state owner :launch)]
        ;; Listen for menu selections
        (go (loop []
              (let [op (<! launch)
                    [x y] (om/get-state owner :menu)]
                (om/transact! game #(sim/launch-player-operation % x y op))
                (om/set-state! owner :menu nil)
                (recur))))))

    om/IDidMount
    (did-mount [_]
      (let [ctx (get-render-context owner "game-canvas")]
        (render-grid ctx game owner)))

    om/IDidUpdate
    (did-update [_ _ _]
      (let [ctx (get-render-context owner "game-canvas")]
        (render-grid ctx game owner)))

    om/IRenderState
    (render-state [this {:keys [menu launch active-cell]}]
      (dom/div nil
               (dom/canvas #js {:width 640
                                :height 640
                                :ref "game-canvas"
                                :onClick (fn [e] (canvas-on-click @game owner e))
                                } nil)
               (om/build boon-ui/boons-view game
                         {:init-state {:grid-to-px-fn (partial get-pos-from-cell owner game)}})
               (om/build op-overlay-ui/operations-view game
                         {:init-state {:grid-to-px-fn (partial get-pos-from-cell owner game)}})

               (if-let [[x y] active-cell]
                 (om/build cell-stats/cell-stats-view game
                           {:state {:cell (world/get-cell game x y)}}))

               (if-let [[x y] active-cell]
                 (om/build op-menu/menu-view game
                               {:init-state {:launch launch
                                             :pos [0 800]}
                                :state {:active-cell [x y]}}))))))

(defn create-ui [game]
  (om/root canvas-view game
           {:target (. js/document (getElementById "canvas-wrapper"))}))
