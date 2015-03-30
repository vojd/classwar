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

(ns classwar.world
  (:require [clojure.string :as str]))

(def GRID_WIDTH 16)
(def GRID_HEIGHT 16)

(def BOON_DURATION 5)
(def ACTIVIST_DAILY_DONATION 1)

(defn- initial-cell-state []
  {:fascists (rand)})

(defn create-world-state []
  "Create the initial game world"
  (let [gs {:time                       1  ;; Game time
            :state                   :new  ;; :new :running :paused :game-over
            :width GRID_WIDTH
            :height GRID_HEIGHT

            :activists                  5  ;; Number of
            :money                    100  ;; $$

            :institutions             #{}  ;; Support groups and structures
            :operations               #{}  ;; Running operations
            :boons                    #{}} ;; Rewards that must be collected

        cells (repeatedly (* GRID_WIDTH GRID_HEIGHT) initial-cell-state)]

    (assoc gs :grid (vec cells))))

(defn- idx [x y] (+ x (* y GRID_WIDTH)))

(defn get-cell [world x y]
  (get-in world [:grid (idx x y)]))

(defn pprint-world [w]
  (.log js/console "Time " (w :time) "  --  Game Overview  --  State: " (str (w :state)))
  (.log js/console " Activists: " (w :activists) "  Money: " (w :money))
  (.log js/console " Operations:" (str/join ", " (map :id (w :operations))))
  (.log js/console " Boons:" (str/join ", " (w :boons))))
