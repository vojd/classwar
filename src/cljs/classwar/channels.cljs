(ns classwar.channels
  (:require    [cljs.core.async :as async]))

(defonce cmd-chan (async/chan))
