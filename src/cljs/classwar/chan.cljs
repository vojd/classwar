(ns classwar.chan
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs.core.async :as async]))

(defonce cmd-chan (async/chan))
(defonce event-chan (async/chan))
