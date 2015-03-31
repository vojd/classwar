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

(ns classwar.ui.boon
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! put!]]
            [classwar.operations :as ops]
            [classwar.world :as world]
            [classwar.simulation :as sim]))

(defn collect-boon-clicked [game gx gy]
  (om/transact! game #(sim/collect-boons % gx gy)))

(defn boon-view [game grid-to-px-fn boon]
  (let [[gx gy :as grid-pos] (:pos boon)
        [x y] (grid-to-px-fn grid-pos)]
    (dom/button #js {:onClick #(collect-boon-clicked game gx gy)
                     :style #js {:position "absolute"
                                 :top x
                                 :left y}}
             ;; created 70, :pos [2 0], :recruitable 1, :money 0}
             (str boon))))

(defn boons-view [game owner]
  (reify om/IRenderState
    (render-state [this {:keys [grid-to-px-fn]}]
      (apply dom/div nil (map #(boon-view game grid-to-px-fn %) (:boons game))))))
