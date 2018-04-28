(ns clj-cli-images.utils)

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; UTILS

(defn sorted-image-map []
  (sorted-map-by (fn [[x1 y1] [x2 y2]]
                   (compare [y1 x1] [y2 x2]))))

(defn parse-line
  [line]
  (mapv #(some identity %)
        (map rest (re-seq #"'([^']+)'|\"([^\"]+)\"|(\S+)" line))))

(defn read-colour
  [colour-string]
  (re-matches #"[A-Z]" colour-string))

(defn read-number
  [num-string]
  (let [n (read-string num-string)]
    (if (number? n) n nil)))