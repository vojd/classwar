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

(ns classwar.simulation
  (:require [classwar.world :as world]
            [classwar.operations :as ops]))

(defn start [g]
  (assoc-in g [:state] :running))
(defn pause [g]
  (assoc-in g [:state] :paused))
(defn resume [g]
  (assoc-in g [:state] :running))

(defn- execute-operations [world]
  (let [bind-op #(partial (:op %) %)
        op-fns (map bind-op (:operations world))]
    ((apply comp op-fns) world)))

(defn- finish-n-reward [op world]
  (let [boon ((:boon op) op world)]
    (-> world
        (update-in [:activists] + (:effort op))
        (update-in [:operations] disj op)
        (update-in [:boons] conj boon))))

(defn- finish-operations [world]
  (let [finished-op? (fn [time op] (>= (- time (:start op)) (:duration op)))
        finished-now? (partial finished-op? (:time world))
        finishing-ops (filter finished-now? (:operations world))
        finish-op-fns (map (fn [op] (partial finish-n-reward op)) finishing-ops)]
    ((apply comp finish-op-fns) world)))

(defn- expired-boon? [time boon]
  (>= (- time (:created boon)) world/BOON_DURATION))

(defn- expire-boons [world]
  (let [expired-now? (partial expired-boon? (:time world))
        expired-boons (filter expired-now? (:boons world))]
    (apply update-in world [:boons] disj expired-boons)))

(defn- collect-boon [b world]
  (-> world
      (update-in [:activists] + (:recruitable b))
      (update-in [:money] + (:money b))
      (update-in [:boons] disj b)))

(defn collect-boons [world x y]
  (let [boons (filter (fn [b] (= [x y] (:pos b))) (:boons world))
        collect-boon-fns (map (fn [b] (partial collect-boon b)) boons)]
    ((apply comp collect-boon-fns) world)))

(defn collect-money [world]
  (update-in world [:money] + (* (:activists world) world/ACTIVIST_DAILY_DONATION)))

(defn tic [world]
  "Advance the world state one tic - run the world logic"
  (world/pprint-world world)
  (if (= (:state world) :running)
    (-> world
        (execute-operations)
        (finish-operations)
        (expire-boons)
        (collect-money)
        (update-in [:time] inc))
    world))

(defn can-launch-operation [g x y op]
  (and (>= (:activists g) (ops/effort op))
       (>= (:money g) (ops/cost op))))

(defn all-available-operations [g x y]
  (filter (partial can-launch-operation g x y) ops/all-ops))

(defn launch-operation [g x y op]
  {:pre [(can-launch-operation g x y op)]}

  (let [new-op (merge op {:start (:time g)
                          :pos [x y]})]
    (-> g
        (update-in [:activists] - (ops/effort op))
        (update-in [:money] - (ops/cost op))
        (update-in [:operations] conj new-op))))
