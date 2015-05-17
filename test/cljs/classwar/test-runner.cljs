(ns classwar.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [classwar.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'classwar.core-test))
    0
    1))
