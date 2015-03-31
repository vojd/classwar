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

(ns classwar.ui.stats
    (:require [om.core :as om :include-macros true]
              [om.dom :as dom :include-macros true]))

(defn stats-money-view [game]
  (dom/div nil
           (dom/div nil "Money: ")
           (dom/div nil (:money game))))

(defn stats-activists-view [game]
  (dom/div nil
           (dom/div nil "Activists: ")
           (dom/div nil (:activists game))))


(defn stats-view [game owner]
  (reify
    om/IDidUpdate
    (did-update [_ _ _]
      (.log js/console "stats-view update"))
    om/IRenderState
    (render-state [this state]
      (dom/div nil
               (stats-money-view game)
               (stats-activists-view game)
               ))))

(defn create-ui [game]
  (om/root stats-view game
           {:target (. js/document (getElementById "stats"))}))
