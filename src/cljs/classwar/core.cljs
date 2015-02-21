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
  (:require    [cljs.core.async :as async]
               [om.core :as om :include-macros true]
               [om.dom :as dom :include-macros true]
               [big-bang.core :refer [big-bang!]]
               [classwar.world :as world]
               [classwar.render :as render]
               [classwar.state :as state])

  (:require-macros [cljs.core.async.macros :refer [go]]))

;; defonce will help us prevent game state to reload whenever we refresh the browser
;;(defonce game-state (state/initial-game-state))

(defn get-render-context [canvas-id]
  (let [canvas (.getElementById js/document canvas-id)]
    (.getContext canvas "2d")))

(defn get-cell [state x y]
  (let [width (-> state :map :width)
        idx (+ x (* y width))]
    (nth (-> state :map :cells ) idx)))

(defn now [] (.getTime (js/Date.)))

(defn update-game [game]
  (state/pprint-game game)
  (state/tic game))

(defn request-update [event {:keys [last-tic dt] :as game}]
  (let [since-last (- (now) last-tic)
        ticks (quot since-last dt)]
    (if (> ticks 0)
      ;; Tick away n times to catch up if nesseccary
      (-> (nth (iterate update-game game) ticks)
          (update-in [:last-tic] (partial + (* ticks dt))))
      game)))

(defn init-ui-state []
  (merge {:dt 1000 :last-tic (now)}
         (state/initial-game-state)))


(def cmd-chan (async/chan))

(defn send-start-antifa-op [x y]
  ;; This is just for debugging - should be hooked up to ui
  (async/put! cmd-chan {:msg-id :start-op
                        :op state/antifa-flyers
                        :pos [x y]}))

(defn send-collect-boon [x y]
  ;; This is just for debugging - should be hooked up to ui
  (async/put! cmd-chan {:msg-id :collect-boon
                        :pos [x y]}))

(defn send-start-game []
  (async/put! cmd-chan {:msg-id :start-game}))
(defn send-pause-game []
  (async/put! cmd-chan {:msg-id :pause-game}))
(defn send-resume-game []
  (async/put! cmd-chan {:msg-id :resume-game}))

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

;; called from index.html
(defn main []
  (go
    (let [ctx (get-render-context "canvas")]
      (big-bang!
       :initial-state (init-ui-state)
       :on-tick request-update
       :to-draw (partial render/render ctx)
       :on-receive incomming-cmd
       :receive-channel cmd-chan
       ))))
