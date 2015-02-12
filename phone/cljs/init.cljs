(ns cljs.classwar.init
  (:require [classwar.core :as core]))

(defn ^:export onDeviceReady []
  (core/main))

(defn ^:export initialize []
  (.addEventListener js/document "deviceready" onDeviceReady true))

(initialize)
