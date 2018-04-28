(ns clj-cli-images.core
  (:require [clj-cli-images.handle :refer [handle-command]]
            [clj-cli-images.read :refer [read-command]]
            [clj-cli-images.utils :refer [parse-line]]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; INIT

(defn -main
  [& args]
  (println "Command Line Interactive Graphical Editor.")
  (loop [state {}]
    (println "\nReady for command:")
    (let [command (parse-line (read-line))
          result  (-> (read-command command)
                      (handle-command state))]
      (when-not (= result :quit)
        (recur result)))))