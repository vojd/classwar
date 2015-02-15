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
  (:require    [cljs.core.async :refer [<!] :as async]
               [om.core :as om :include-macros true]
               [om.dom :as dom :include-macros true]
               [big-bang.core :refer [big-bang!]]
               [classwar.world :as world]
               [classwar.render :as render]
               [classwar.state :as state])

  (:require-macros [cljs.core.async.macros :refer [go]]))

;; defonce will help us prevent game state to reload whenever we refresh the browser
;;(defonce game-state (state/initial-game-state))

(defn get-render-context [canvas-id]
  (let [canvas (.getElementById js/document canvas-id)]
    (.getContext canvas "2d")))

(defn get-cell [state x y]
  (let [width (-> state :map :width)
        idx (+ x (* y width))]
    (nth (-> state :map :cells ) idx)))


(defn reset-timer [game]
  (assoc-in game [:time] 1))

(defn update-tick [game]
  (update-in game [:time] inc))

(defn update-game [event game]

  (if (= 0 (mod (:time game) (:round-duration game)))
    (-> game
        (reset-timer))

    ;; else
    (update-tick game)))

;; called from index.html
(defn main []
  (go
    (let [ctx (get-render-context "canvas")]

      (big-bang!
       :initial-state (state/initial-game-state)
       :on-tick update-game
       :to-draw (partial render/render ctx)))))
