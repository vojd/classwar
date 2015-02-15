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

(ns classwar.render
  (:require [classwar.state :as state]))

(defn rgb-str [v]
  (let [fascists-rgb (int (* 255 (:fascists v)))]
    (str "rgb(0, 0, " fascists-rgb ")")))

(defn render-grid [ctx state]
  (let [w (vec (range (:width state)))
        h (vec (range (:height state)))
        cell-size (:cell-size state)]
    (doseq [x w
            y h]
      (let [val (state/get-cell state x y)]
        (set! (. ctx -fillStyle) (rgb-str val))
        (.fillRect ctx (* x cell-size) (* y cell-size) cell-size cell-size))))
  state)

(defn render [ctx game]
  (render-grid ctx game))