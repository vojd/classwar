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
            [cljs.core.async :refer [put!]]
            [classwar.simulation :as sim]))

(defmulti op-button-name :id)
(defmethod op-button-name :antifa-flyers [op]
  "Flyers")
(defmethod op-button-name :antifa-demo [op]
  "Demo")

(defn menu-option [{:keys [launch-chan op active-cell]} owner]
  (reify
    om/IRender
    (render [this]
      (dom/div #js {:onClick #(put! launch-chan op)
                    :className "menu-option"}
               (dom/div #js {:className "menu-option-icon"}
                        (dom/img #js {:src "img/activist.svg"}))

               (dom/div #js {:className "menu-option-content"}
                        (op-button-name op))))))

(defn menu-view [game owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [launch pos active-cell]}]
      (let [[x y] (om/get-state owner :pos)]
        (apply
         dom/div #js { :style #js {:position "absolute"
                                   :top x
                                   :left y
                                   :border "1px solid #303030"
                                   :width "120px"
                                   :height "100%"}}

         (let [[gridx gridy] active-cell
               available-ops (sim/all-available-operations game gridx gridy)]
           (om/build-all
            menu-option (map (fn [op] {:launch-chan launch
                                      :op op
                                      :active-cell active-cell}) available-ops))))))))
