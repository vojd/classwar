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
(def CELL-SIZE 80)

(defn- initial-cell-state []
  {:fascists (rand)})

(defn initial-game-state []
  "Create the initial game state"
  (let [gs {:time                       1  ;; Game time
            :round-duration             100 ;; Amounts of updates a round lasts
            :state                   :new  ;; :new :running :paused :game-over
            :width GRID_WIDTH
            :height GRID_HEIGHT
            :cell-size CELL-SIZE

            :activists                  5  ;; Number of
            :money                    100  ;; $$

            :institutions             #{}  ;; Support groups and structures
            :operations               #{}} ;; Running operations

        cells (repeatedly (* GRID_WIDTH GRID_HEIGHT) initial-cell-state)]

    (assoc gs :grid (vec cells))))

(defn- idx [x y] (+ x (* y GRID_WIDTH)))

(defn get-cell [game x y]
  (get-in game [:grid (idx x y)]))

(defn- bind-op [op]
  (partial (:op op) op))

(defn- execute-operations [game]
  (let [op-fns (map bind-op (:operations game))]
    ((apply comp op-fns) game)))

(defn tic [game]
  "Advance the game state one tic - run the game logic"
  (-> game
      (execute-operations)
      (update-in [:time] inc)))

(def antifa-flyers {
  :effort 2
  :cost 20
  :duration 5
  :op (fn [{[x y] :pos :as op} game]
        (let [idx (idx x y)
              facist-level-modifier-fn (fn [level] (max 0 (- level 0.1)))]
          (update-in game [:grid idx :fascists] facist-level-modifier-fn)))})

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
