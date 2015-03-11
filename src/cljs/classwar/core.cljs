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
  (:require [classwar.engine :as engine]
            [classwar.grid :as grid]
            [classwar.channels :as channels]
            [classwar.ui.play-ctrls :as fu]))

(defn main []
  (.log js/console ">> Running main << ")
  (let [world (engine/init-engine-state channels/cmd-chan)
        render-fn (partial grid/render (grid/get-render-context "canvas"))]
    (engine/start-game world render-fn)))
