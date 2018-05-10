(ns clj-cli-images.read
  (:require [clj-cli-images.utils :refer :all]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; READING COMMANDS

;TODO: Better way of checking that commands match predicates / schema

(defmulti read-command (fn [[command]] (keyword command)))

(defmethod read-command :I
  [[_ & rest]]
  (if (not (and (= (count rest) 2)
                (every? number? (map read-string rest))))
    {:command :error
     :message "Invalid arguments for command: I"}
    (let [[m n] rest
          cols (read-number m)
          rows (read-number n)]
      (if (and (<= 1 cols 250)
               (<= 1 rows 250))
        {:command :create-image
         :rows    rows
         :cols    cols}
        {:command :error
         :message "Image dimensions should be between 1 and 250 pixels."}))))

(defmethod read-command :C
  [_]
  {:command :clear-image})

(defmethod read-command :L
  [[_ & rest]]
  (if (not (and (= (count rest) 3)
                (every? number? (map read-number (butlast rest)))
                (read-colour (last rest))))
    {:command :error
     :message "Invalid arguments for command: L"}
    (let [[x y c] rest]
      {:command :colour-pixel
       :x       (read-number x)
       :y       (read-number y)
       :colour  (read-colour c)})))

(defmethod read-command :V
  [[_ & rest]]
  (if (not (and (= (count rest) 4)
                (every? number? (map read-number (butlast rest)))
                (read-colour (last rest))))
    {:command :error
     :message "Invalid arguments for command: V"}
    (let [[x y1 y2 c] rest]
      {:command :vertical-segment
       :x       (read-number x)
       :y1      (read-number y1)
       :y2      (read-number y2)
       :colour  (read-colour c)})))

(defmethod read-command :H
  [[_ & rest]]
  (if (not (and (= (count rest) 4)
                (every? number? (map read-number (butlast rest)))
                (read-colour (last rest))))
    {:command :error
     :message "Invalid arguments for command: H"}
    (let [[x1 x2 y c] rest]
      {:command :horizontal-segment
       :x1      (read-number x1)
       :x2      (read-number x2)
       :y       (read-number y)
       :colour  (read-colour c)})))

(defmethod read-command :F
  [[_ & rest]]
  (if (not (and (= (count rest) 3)
                (every? number? (map read-number (butlast rest)))
                (read-colour (last rest))))
    {:command :error
     :message "Invalid arguments for command: F"}
    (let [[x y c] rest]
      {:command :region
       :x       (read-number x)
       :y       (read-number y)
       :colour  (read-colour c)})))

(defmethod read-command :R
  [[_ & rest]]
  (if (not (and (> (count rest) 2)
                (every? number? (map read-number (take 2 rest)))
                (map read-colour (drop 2 rest))))
    {:command :error
     :message "Invalid arguments for command: R"}
    (let [[x y & colours] rest]
      {:command :radius
       :x       (read-number x)
       :y       (read-number y)
       :colours (mapv read-colour colours)})))

(defmethod read-command :S
  [_]
  {:command :show-image})

(defmethod read-command :X
  [_]
  {:command :quit})

(defmethod read-command :default
  [[command]]
  {:command :error
   :message (str "Unrecognised command: " command)})