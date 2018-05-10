(ns clj-cli-images.handle
  (:require [clj-cli-images.utils :as utils]
            [clojure.math.numeric-tower :refer [abs]]
            [clojure.set :as set]
            [clojure.string :as str]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; HANDLING COMMANDS

(defmulti handle-command (fn [command-map state] (:command command-map)))

(defmethod handle-command :create-image
  [{:keys [:rows :cols]} _]
  (let [pixel-coords (for [x (range 1 (inc rows))
                           y (range 1 (inc cols))]
                       [x y])
        image-map (zipmap pixel-coords (repeat "O"))]
    {:image image-map
     :rows  rows
     :cols  cols}))

(defmethod handle-command :clear-image
  [_ {:keys [:rows :cols]}]
  (handle-command {:command :create-image
                   :rows    rows
                   :cols    cols}
                  {}))

(defmethod handle-command :colour-pixel
  [{:keys [:x :y :colour]} state]
  (assoc-in state [:image [x y]] colour))

(defmethod handle-command :vertical-segment
  [{:keys [:x :y1 :y2 :colour]} {:keys [:image] :as state}]
  (let [target-pixels (map #(vector x %) (range y1 (inc y2)))
        new-pixels    (zipmap target-pixels (repeat colour))
        new-image     (merge image new-pixels)]
    (assoc state :image new-image)))

(defmethod handle-command :horizontal-segment
  [{:keys [:x1 :x2 :y :colour]} {:keys [:image] :as state}]
  (let [target-pixels (map #(vector % y) (range x1 (inc x2)))
        new-pixels    (zipmap target-pixels (repeat colour))
        new-image     (merge image new-pixels)]
    (assoc state :image new-image)))

(defn- get-same-colour-neighbours
  [pixel image]
  (let [[[x y] colour] pixel
        touching-pixel-coords  (->> (for [dx [-1 0 1]
                                          dy [-1 0 1]]
                                      (when (= 1 (abs (- dx dy)))
                                        [(+ x dx) (+ y dy)]))
                                    (remove nil?))
        touching-pixels        (select-keys image touching-pixel-coords)
        same-colour-neighbours (->> touching-pixels
                                    (filter (fn [[_ c]] (= colour c)))
                                    (into {[x y] colour}))]
    same-colour-neighbours))

(defn- get-same-colour-region
  [coords image]
  (loop [region (select-keys image [coords])
         tested-pixels (set [])]
    (let [just-tested-pixels     (into tested-pixels (keys region))
          untested-pixels-coords (set/difference (set (keys region))
                                                 tested-pixels)
          untested-pixels        (select-keys region untested-pixels-coords)
          matching-region        (->> (map #(get-same-colour-neighbours % image) untested-pixels)
                                      (reduce merge region))]
      (if (= tested-pixels just-tested-pixels)
        matching-region
        (recur matching-region just-tested-pixels)))))

(defmethod handle-command :region
  [{:keys [:x :y :colour]} {:keys [:image] :as state}]
  (let [target-pixels (keys (get-same-colour-region [x y] image))
        new-pixels    (zipmap target-pixels (repeat colour))
        new-image     (merge image new-pixels)]
    (assoc state :image new-image)))

(defn- get-pixel-coords-at-radius
  [x y r image]
  (->> (for [dx (range (- r) (inc r))
             dy (range (- r) (inc r))]
         (when (or (= (abs dx) r)
                   (= (abs dy) r))
           [(+ x dx) (+ y dy)]))
       (remove nil?)
       (set)
       (set/intersection (set (keys image)))))

(defmethod handle-command :radius
  [{:keys [:x :y :colours]} {:keys [:image] :as state}]
  (let [new-pixels (->> colours
                        (map-indexed (fn [i colour]
                                       (-> (get-pixel-coords-at-radius x y i image)
                                           (zipmap (repeat colour)))))
                        (reduce merge))
        new-image (merge image new-pixels)]
    (assoc state :image new-image)))

(defmethod handle-command :show-image
  [_ {:keys [:cols :image] :as state}]
  (->> (into (utils/sorted-image-map) image)
       (partition-all cols)
       (map #(str/join "" (vals %)))
       (str/join "\n")
       println)
  state)

(defmethod handle-command :quit
  [_ _]
  (println "Bye for now!")
  :quit)

(defmethod handle-command :error
  [{:keys [:message]} state]
  (println (str/join  "" ["Error:" "\n" message "\n"]))
  state)
