(ns clj-cli-images.read_test
  (:require [clj-cli-images.read :refer [read-command]]
            [clojure.test :refer :all]))

(deftest create-image-command-test
  (testing "issues command to create a new image image"
    (is (= {:command :create-image
            :rows    4
            :cols    4}
           (read-command ["I" "4" "4"]))))

  (testing "issues command to error when input is invalid"
    (is (= :error
           (:command (read-command ["I" "4" "X"]))))))

(deftest colour-pixel-command-test
  (testing "issues command to colour an individual pixel"
    (is (= {:command :colour-pixel
            :x       4
            :y       4
            :colour  "P"}
           (read-command ["L" "4" "4" "P"]))))

  (testing "issues command to error when colour is invalid"
    (is (= :error
           (:command (read-command ["L" "4" "4" "3"]))))))

(deftest vertical-segment-command-test
  (testing "issues command to colour a vertical region"
    (is (= {:command :vertical-segment
            :x       2
            :y1      1
            :y2      4
            :colour  "P"}
           (read-command ["V" "2" "1" "4" "P"]))))

  (testing "issues command to error when wrong number of args"
    (is (= :error
           (:command (read-command ["V" "2" "1" "4"]))))))

(deftest horizontal-segment-command-test
  (testing "issues command to colour a horizontal region"
    (is (= {:command :horizontal-segment
            :x1      2
            :x2      1
            :y       4
            :colour  "P"}
           (read-command ["H" "2" "1" "4" "P"]))))

  (testing "issues command to error when coordinate is invalid"
    (is (= :error
           (:command (read-command ["V" "P" "1" "4" "P"]))))))

(deftest region-command-test
  (testing "issues command to colour an existing region"
    (is (= {:command :region
            :x       4
            :y       4
            :colour  "P"}
           (read-command ["F" "4" "4" "P"])))))

(deftest radius-command-test
  (testing "issues command to colour colour concentric squares"
    (is (= {:command :radius
            :x       4
            :y       4
            :colours ["M" "P"]}
           (read-command ["R" "4" "4" "M" "P"])))))

(deftest error-command-test
  (testing "issues command to error when input not recognised"
    (is (= :error
           (:command (read-command ["do a thing"]))))))
