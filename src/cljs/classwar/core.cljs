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
  (:require [cljs.core.async :as async]
            [classwar.world :as world]
            [classwar.clock :as clock]
            [classwar.ui.grid :as ui-grid]
            [classwar.ui.play-ctrls :as ui-ctrls]
            [classwar.ui.stats :as ui-stats]))

(def game (atom (world/create-world-state)))

(defn main []
  (.log js/console ">> Running main << ")
  (clock/start-game-clock game 1000)
  (ui-grid/create-ui game)
  (ui-stats/create-ui game)
  (ui-ctrls/create-ui game))
