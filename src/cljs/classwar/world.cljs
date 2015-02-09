(ns classwar.world)

(defn generate-cells [w h]
  (for [x (vec (range w))
        y (vec (range h))]
    (assoc {} :val (int (* 255 (rand))))))


(defn generate-world []
  (let [w 8
        h 8
        cell-size 80]
    {:map {:width w
           :height h
           :cell-size cell-size
           :cells (generate-cells w h)}}))
