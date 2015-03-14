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
            [classwar.engine :as engine]
            [classwar.ui.state :as ui-state]
            [classwar.ui.grid :as grid]
            [classwar.ui.play-ctrls :as fu]))

(defn main []
  (.log js/console ">> Running main << ")
  (let [cmd-chan (async/chan)
        world (engine/init-engine-state cmd-chan)
        render-fn (partial grid/render (grid/get-render-context "canvas"))]
    (swap! ui-state/ui-state assoc :cmd-chan cmd-chan)
    (engine/start-game world render-fn)))
