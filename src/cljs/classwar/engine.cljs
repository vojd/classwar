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

(defn- incomming-cmd [{:keys [msg-id] :as event} world]
  (.log js/console "incomming-cmd!")
  (condp = msg-id
    :tick (swap! world update-game)
    :start-game (swap! world state/start)
    :pause-game (swap! world state/pause)
    :resume-game (swap! world state/resume)
    :start-op
    (let [[x y] (:pos event)]
      (swap! world state/launch-operation x y (:op event)))
    :collect-boon
    (let [[x y] (:pos event)]
      (swap! world state/collect-boons x y)))
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
