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
               [classwar.world :as world]
               [classwar.state :as state]
               [classwar.channels :as channels]
               [classwar.ui.play-ctrls :as fu]
               )

  (:require-macros [cljs.core.async.macros :refer [go]]))

(defn now [] (.getTime (js/Date.)))

(defn init-engine-state [cmd-chan event-chan]
  (merge {:cmd-chan cmd-chan
          :event-chan event-chan}
         {:dt 1000
          :last-tic (now)}
         (state/initial-game-state)))

(defn update-game [game]
  (state/pprint-game game)
  (let [event-chan (:event-chan game)
        new-game (state/tic game)]
    (async/put! event-chan {:msg-id :tic :world new-game})
    new-game))

(defn request-update [event {:keys [last-tic dt] :as game}]
  (let [since-last (- (now) last-tic)
        ticks (quot since-last dt)]
    (if (> ticks 0)
      ;; Tick away n times to catch up if nesseccary
      (-> (nth (iterate update-game game) ticks)
          (update-in [:last-tic] (partial + (* ticks dt))))
      game)))

(defn incomming-cmd [{:keys [msg-id] :as event} world]
  (.log js/console "incomming-cmd!")
  (condp = msg-id
    :start-game (state/start world)
    :pause-game (state/pause world)
    :resume-game (state/resume world)
    :start-op
    (let [[x y] (:pos event)]
      (state/launch-operation world x y (:op event)))
    :collect-boon
    (let [[x y] (:pos event)]
      (state/collect-boons world x y))))

(defn start-game [world render-fn]
  (go
    (big-bang!
     :initial-state world
     :on-tick request-update
     :to-draw render-fn
     :on-receive incomming-cmd
     :receive-channel (:cmd-chan world))))
