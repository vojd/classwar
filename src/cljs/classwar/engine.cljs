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

(ns classwar.engine
  (:require    [cljs.core.async :as async]
               [big-bang.core :refer [big-bang!]]
               [classwar.simulation :as sim]
               [classwar.world :as world])
  (:require-macros [cljs.core.async.macros :refer [go]]))

(def cmd-chan (async/chan))

(defn start-ticker [cmd-chan period]
  (go (while true
        (async/put! cmd-chan {:msg-id :tick})
        (async/<! (async/timeout period)))))

(defmulti process-cmd :msg-id)
(defmethod process-cmd :tick [cmd game]
  (sim/tic game))

(defmethod process-cmd :collect-boon [cmd game]
  (let [{[x y] :pos} cmd]
    (sim/collect-boons game x y)))

(defn handle-incomming-cmd [cmd game]
  (swap! game #(process-cmd cmd %))
  game)

(defn start-game [game cmd-chan]
  (go
    (big-bang!
     :initial-state game
     :on-receive handle-incomming-cmd
     :receive-channel cmd-chan)
    (start-ticker cmd-chan 1000)))
