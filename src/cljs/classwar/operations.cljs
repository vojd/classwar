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

(ns classwar.operations
  (:require [classwar.world :as world]))

(def antifa-flyers {
  :id :antifa-flyers
  :agent :player
  :effort 2
  :cost 20
  :duration 5
  :op (fn [{[x y] :pos :as op} world]
        (let [idx (world/idx x y)
              fascist-level-modifier-fn (fn [level] (max 0 (- level 0.1)))]
          (update-in world [:grid idx :fascists] fascist-level-modifier-fn)))
  :boon (fn [{pos :pos :as op} world] {
          :created (:time world)
          :pos pos
          :recruitable 1
          :money 0})})

(def antifa-demo {
  :id :antifa-demo
  :agent :player
  :effort 10
  :cost 100
  :duration 5
  :op (fn [{[x y] :pos :as op} world]
        (let [idx (world/idx x y)
              fascist-level-modifier-fn (fn [level] (max 0 (- level 0.2)))]
          (update-in world [:grid idx :fascists] fascist-level-modifier-fn)))
  :boon (fn [{pos :pos :as op} world] {
          :created (:time world)
          :pos pos
          :recruitable 2
          :money 0})})

(def fascist-flyers {
  :id :fascist-flyers
  :agent :fascist
  :duration 5
  :op (fn [{[x y] :pos :as op} world]
        (let [idx (world/idx x y)
              fascist-level-modifier-fn (fn [level] (min 1 (+ level 0.1)))]
          (update-in world [:grid idx :fascists] fascist-level-modifier-fn)))})


(def all-ops [antifa-flyers antifa-demo fascist-flyers])

(defn player-op? [op] (= (:agent op) :player))
(defn fascist-op? [op] (= (:agent op) :fascist))

(defn cost [op] (get op :cost 0))
(defn effort [op] (get op :effort 0))
