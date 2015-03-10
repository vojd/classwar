(ns classwar.channels
  (:require    [cljs.core.async :as async]))

(defonce event-chan (async/chan))
(defonce cmd-chan (async/chan))
