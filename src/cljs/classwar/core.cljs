(ns classwar.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [classwar.world :as world]))

;; defonce will help us prevent game state to reload whenever we refresh the browser
(defonce game-state (atom (world/generate-world)))

(defn get-render-context [canvas-id]
  (let [canvas (.getElementById js/document canvas-id)]
    (.getContext canvas "2d")))

(defn get-cell [world x y]
  (let [width (-> @world :map :width)
        idx (+ x (* y width))]
    (nth (-> @world :map :cells ) idx)))

(defn render-game-grid [world]
  (let [w (vec (range (-> @world :map :width)))
        h (vec (range (-> @world :map :height)))
        cell-size (-> @world :map :cell-size)
        ctx (get-render-context "canvas")]
    (doseq [x w
            y h]
      (let [val (get-cell world x y)]
        (set! (. ctx -fillStyle) (str "rgb(" (:val val) ", 0, 0"))
        (.fillRect ctx (* x cell-size) (* y cell-size) cell-size cell-size))))
  world)

;; called from index.html
(defn main []
  (render-game-grid game-state))
