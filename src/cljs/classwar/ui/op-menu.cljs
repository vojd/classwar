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

(ns classwar.ui.op-menu
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [<! put!]]
            [classwar.engine :as engine]
            [classwar.operations :as ops]
            [classwar.world :as world]))

(defn menu-view [game owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [launch menu]}]
      (let [[x y] (om/get-state owner :pos)]
        (dom/div #js { :style #js {:position "absolute"
                                   :top x
                                   :left y}}
                 (dom/button #js { :onClick (fn [game event]
                                              (put! launch ops/antifa-flyers))} "Flyers")
                 (dom/button #js { :onClick (fn [game event]
                                              (put! launch ops/antifa-demo))} "Demo"))))))
