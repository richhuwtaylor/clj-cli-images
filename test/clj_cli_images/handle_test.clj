(ns clj-cli-images.handle_test
  (:require [clj-cli-images.handle :refer [handle-command]]
            [clojure.string :as str]
            [clojure.test :refer :all]))

(def test-state
  {:image {[1 1] "O" [2 1] "O" [3 1] "O" [4 1] "O"
           [1 2] "O" [2 2] "O" [3 2] "O" [4 2] "O"
           [1 3] "O" [2 3] "O" [3 3] "O" [4 3] "O"
           [1 4] "O" [2 4] "O" [3 4] "O" [4 4] "O"}
   :rows  4
   :cols  4})

(def test-state-region
  {:image {[1 1] "O" [2 1] "O" [3 1] "O" [4 1] "O"
           [1 2] "O" [2 2] "M" [3 2] "M" [4 2] "O"
           [1 3] "O" [2 3] "M" [3 3] "M" [4 3] "O"
           [1 4] "O" [2 4] "O" [3 4] "O" [4 4] "O"}
   :rows  4
   :cols  4})

(def test-state-radius
  {:image {[1 1] "P" [2 1] "P" [3 1] "P" [4 1] "O"
           [1 2] "P" [2 2] "M" [3 2] "P" [4 2] "O"
           [1 3] "P" [2 3] "P" [3 3] "P" [4 3] "O"
           [1 4] "O" [2 4] "O" [3 4] "O" [4 4] "O"}
   :rows  4
   :cols  4})

(deftest create-image-test
  (testing "creates an image"
    (is (= test-state (handle-command
                        {:command :create-image
                         :rows    4
                         :cols    4}
                        {})))))

(deftest colour-pixel-test
  (testing "colours an individual pixel"
    (is (= "P"
           (get (:image (handle-command
                          {:command :colour-pixel
                           :x       4
                           :y       3
                           :colour  "P"}
                          test-state))
                [4 3])))))

(deftest vertical-segment-test
  (testing "colours a vertical segment"
    (is (= {[1 1] "P"
            [1 2] "P"
            [1 3] "P"
            [1 4] "P"}
           (select-keys (:image (handle-command
                                  {:command :vertical-segment
                                   :x       1
                                   :y1      1
                                   :y2      4
                                   :colour  "P"}
                                  test-state))
                        [[1 1] [1 2] [1 3] [1 4]])))))

(deftest horizontal-segment-test
  (testing "colours a horizontal segment"
    (is (= {[1 1] "P" [2 1] "P" [3 1] "P" [4 1] "P"}
           (select-keys (:image (handle-command
                                  {:command :horizontal-segment
                                   :x1      1
                                   :x2      4
                                   :y       1
                                   :colour  "P"}
                                  test-state))
                        [[1 1] [2 1] [3 1] [4 1]])))))

(deftest region-test
  (testing "colours an existing region"
    (is (= {[2 2] "P" [3 2] "P" [2 3] "P" [3 3] "P"}
           (select-keys (:image (handle-command
                                  {:command :region
                                   :x       2
                                   :y       2
                                   :colour  "P"}
                                  test-state-region))
                        [[2 2] [3 2] [2 3] [3 3]])))))

(deftest radius-test
  (testing "colours pixels in concentric squares correctly"
    (is (= {[2 2] "M" [3 2] "P" [2 3] "P" [3 3] "P"}
           (select-keys (:image (handle-command
                                  {:command :radius
                                   :x       2
                                   :y       2
                                   :colours ["M" "P"]}
                                  test-state-region))
                        [[2 2] [3 2] [2 3] [3 3]])))))

(deftest clear-image-test
  (testing "clears the image after colouring an individual pixel"
    (is (= test-state
           (->> test-state
                (handle-command
                  {:command :colour-pixel
                   :x       4
                   :y       3
                   :colour  "P"})
                (handle-command
                  {:command :clear-image}))))))

(deftest quit-test
  (testing "quits on command"
    (is (= :quit
           (handle-command
             {:command :quit}
             {})))))