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

(ns classwar.state
  (:require [clojure.string :as str]))

(def GRID_WIDTH 16)
(def GRID_HEIGHT 16)
(def CELL-SIZE 80)

(defn- initial-cell-state []
  {:fascists (rand)})


(defn initial-game-state []
  "Create the initial game state"
  (let [gs {:time                       1  ;; Game time
            :state                   :new  ;; :new :running :paused :game-over
            :width GRID_WIDTH
            :height GRID_HEIGHT
            :cell-size CELL-SIZE

            :activists                  5  ;; Number of
            :money                    100  ;; $$

            :institutions             #{}  ;; Support groups and structures
            :operations               #{}  ;; Running operations
            :boons                    #{}} ;; Rewards that must be collected

        cells (repeatedly (* GRID_WIDTH GRID_HEIGHT) initial-cell-state)]

    (assoc gs :grid (vec cells))))

(defn start [g]
  (assoc-in g [:state] :running))
(defn pause [g]
  (assoc-in g [:state] :paused))
(defn resume [g]
  (assoc-in g [:state] :running))

(defn- idx [x y] (+ x (* y GRID_WIDTH)))

(defn get-cell [game x y]
  (get-in game [:grid (idx x y)]))

(defn- bind-op [op]
  (partial (:op op) op))

(defn- execute-operations [game]
  (let [op-fns (map bind-op (:operations game))]
    ((apply comp op-fns) game)))

(defn- finished-op? [time op]
  (>= (- time (:start op)) (:duration op)))

(defn- finish-n-reward [op game]
  (let [boon ((:boon op) op game)]
    (-> game
        (update-in [:activists] + (:effort op))
        (update-in [:operations] disj op)
        (update-in [:boons] conj boon))))

(defn- finish-operations [game]
  (let [finished-now? (partial finished-op? (:time game))
        finishing-ops (filter finished-now? (:operations game))
        finish-op-fns (map (fn [op] (partial finish-n-reward op)) finishing-ops)]
    ((apply comp finish-op-fns) game)))

(def BOON_DURATION 5)

(defn- expired-boon? [time boon]
  (>= (- time (:created boon)) BOON_DURATION))

(defn- expire-boons [game]
  (let [expired-now? (partial expired-boon? (:time game))
        expired-boons (filter expired-now? (:boons game))]
    (apply update-in game [:boons] disj expired-boons)))

(defn- collect-boon [b game]
  (-> game
      (update-in [:activists] + (:recruitable b))
      (update-in [:money] + (:money b))
      (update-in [:boons] disj b)))

(defn collect-boons [game x y]
  (let [boons (filter (fn [b] (= [x y] (:pos b))) (:boons game))
        collect-boon-fns (map (fn [b] (partial collect-boon b)) boons)]
    ((apply comp collect-boon-fns) game)))

(defn tic [game]
  "Advance the game state one tic - run the game logic"
  (if (= (:state game) :running)
    (-> game
        (execute-operations)
        (finish-operations)
        (expire-boons)
        (update-in [:time] inc))
    game))

(def antifa-flyers {
  :id :antifa-flyers
  :effort 2
  :cost 20
  :duration 5
  :op (fn [{[x y] :pos :as op} game]
        (let [idx (idx x y)
              fascist-level-modifier-fn (fn [level] (max 0 (- level 0.1)))]
          (update-in game [:grid idx :fascists] fascist-level-modifier-fn)))
  :boon (fn [{pos :pos :as op} game] {
          :created (:time game)
          :pos pos
          :recruitable 1
          :money 0})})

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

(defn pprint-game [g]
  (.log js/console "Time " (g :time) "  --  Game Overview  --  State: " (str (g :state)))
  (.log js/console " Activists: " (g :activists) "  Money: " (g :money))
  (.log js/console " Operations:" (str/join ", " (map :id (g :operations))))
  (.log js/console " Boons:" (str/join ", " (g :boons))))

(defn test-game [tics]
  (let [g (initial-game-state)
        gg (launch-operation g 0 0 antifa-flyers)]
    (nth (map pprint-game (iterate tic gg)) tics)))
