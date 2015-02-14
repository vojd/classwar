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

(ns classwar.state)

(def GRID_WIDTH 16)
(def GRID_HEIGHT 16)

(defn- initial-cell-state []
  {:facists (rand)})

(defn initial-game-state []
  "Create the initial game state"
  (let [gs {:time                       0  ;; Game time
            :state                   :new  ;; :new :running :paused :game-over

            :activists                  5  ;; Number of
            :money                    100  ;; $$

            :institutions             #{}  ;; Support groups and structures
            :operations               #{}} ;; Running operations

        cells (repeatedly (* GRID_WIDTH GRID_HEIGHT) initial-cell-state)]

    (assoc gs :grid (vec cells))))

(defn- idx [x y] (+ x (* y GRID_WIDTH)))

(defn get-cell [game x y]
  ((:grid game) (idx x y)))

(defn assoc-cell [game x y new-val]
  (assoc-in game [:grid (idx x y)] new-val))

(defn update-cell [game x y f]
  (update-in game [:grid (idx x y)] f))


(def antifa-flyers {
  :effort 2
  :cost 20
  :duration 5
  :op (fn [g a]
        ;; Do stuff each tic and return the new world
        g)})

(defn cost [op] (get op :cost 0))
(defn effort [op] (get op :effort 0))

(defn launch-operation [g x y op]
  (let [new-op (merge op {
                      :start (:time g)
                      :pos [x y]})]
    (-> g
        (update-in [:activists] - (effort op))
        (update-in [:money] - (cost op))
        (update-in [:operations] conj new-op))))

(def ACTIVIST_DAILY_DONATION 5)
