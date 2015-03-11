(ns classwar.ui.state)

(defonce ui-state (atom {:time 0
                         :cmd-chan nil
                         :event-chan nil
                         :boons [{:n "one" :x 100 :y 20}
                                 {:n "two" :x 200 :y 200}]}))
