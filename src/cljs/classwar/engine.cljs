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
               [classwar.state :as state])

  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn now [] (.getTime (js/Date.)))

(defn init-engine-state [cmd-chan]
  (merge {:cmd-chan cmd-chan}
         (state/initial-game-state)))

(defn- update-game [game]
  (state/pprint-game game)
  (state/tic game))

(defn- send-tick! [cmd-chan]
  (async/put! cmd-chan {:msg-id :tick}))

(defn start-ticker [cmd-chan period]
  (go (while true
        (send-tick! cmd-chan)
        (<! (async/timeout period)))))

(defmulti process-cmd :msg-id)
(defmethod process-cmd :tick [cmd world]
  (update-game world))

(defmethod process-cmd :start-game [cmd world]
  (state/start world))
(defmethod process-cmd :pause-game [cmd world]
  (state/pause world))
(defmethod process-cmd :resume-game [cmd world]
  (state/resume world))

(defmethod process-cmd :start-op [cmd world]
  (let [{[x y] :pos} cmd]
    (state/launch-operation world x y (:op cmd))))

(defmethod process-cmd :collect-boon [cmd world]
  (let [{[x y] :pos} cmd]
    (state/collect-boons world x y)))

(defn incomming-cmd [cmd world]
  (swap! world #(process-cmd cmd %))
  world)

(def cmd-chan (async/chan))
(def game (atom (init-engine-state cmd-chan)))

(defn start-game [world render-fn]
  (go
    (big-bang!
     :initial-state world
     :to-draw render-fn
     :on-receive incomming-cmd
     :receive-channel (:cmd-chan @world))
    (start-ticker (:cmd-chan @world) 1000)))
