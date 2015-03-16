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
               [classwar.world :as world])

  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn now [] (.getTime (js/Date.)))

(defn- update-game [game]
  (world/pprint-world game)
  (world/tic game))

(defn- send-tick! [cmd-chan]
  (async/put! cmd-chan {:msg-id :tick}))

(defn start-ticker [cmd-chan period]
  (go (while true
        (send-tick! cmd-chan)
        (<! (async/timeout period)))))

(defmulti process-cmd :msg-id)
(defmethod process-cmd :tick [cmd game]
  (update-game game))

(defmethod process-cmd :start-game [cmd game]
  (world/start game))
(defmethod process-cmd :pause-game [cmd game]
  (world/pause game))
(defmethod process-cmd :resume-game [cmd game]
  (world/resume game))

(defmethod process-cmd :start-op [cmd game]
  (let [{[x y] :pos} cmd]
    (world/launch-operation game x y (:op cmd))))

(defmethod process-cmd :collect-boon [cmd game]
  (let [{[x y] :pos} cmd]
    (world/collect-boons game x y)))

(defn incomming-cmd [cmd game]
  (swap! game #(process-cmd cmd %))
  game)

(defn create-game-state [cmd-chan]
  (merge {:cmd-chan cmd-chan}
         (world/create-world-state)))

(def cmd-chan (async/chan))
(def game (atom (create-game-state cmd-chan)))

(defn start-game [game render-fn]
  (go
    (big-bang!
     :initial-state game
     :to-draw render-fn
     :on-receive incomming-cmd
     :receive-channel (:cmd-chan @game))
    (start-ticker (:cmd-chan @game) 1000)))
