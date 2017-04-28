(ns log-parser.core-test
  (:require [clojure.test :refer :all]
            [log-parser.core :refer :all]))

(deftest load-lines-test
  (testing "Test fetch all the lines"
    (is (= 5 (count (load-lines "log-test.txt"))))))

(deftest clean-lines-test
  (testing "Testing clean-lines"
    (is (= '(("request_to=\"https://grotesquemoon.de\"" "response_status=\"201\"")
             ("request_to=\"https://woodenoyster.com.br\"" "response_status=\"503\"")
             ("request_to=\"https://grimpottery.net.br\"" "response_status=\"400\""))
           (clean-lines (load-lines "log-test.txt"))))))

(deftest extract-info-test
  (testing "Testing extract-info"
    (is (= '("\"https://grotesquemoon.de\"" "\"201\"")
           (extract-info '("request_to=\"https://grotesquemoon.de\"" "response_status=\"201\""))))))

(deftest remove-quotes-test
  (testing "Testing remove-quotes"
    (is (= '("https://grotesquemoon.de" "201")
           (remove-quotes '("\"https://grotesquemoon.de\"" "\"201\""))))))

(deftest filter-response-codes-test
  (testing "Testing filter-response-codes"
    (is (= '("201" "503")
           (filter-response-codes '("https://grotesquemoon.de" "201" "https://woodenoyster.com.br" "503"))))))

(deftest filter-urls-test
  (testing "Testing filter-urls"
    (is (= '("https://grotesquemoon.de" "https://woodenoyster.com.br")
           (filter-urls '("https://grotesquemoon.de" "201" "https://woodenoyster.com.br" "503"))))))

(deftest count-occurrences-test
  (testing "Testing count-occurrences for different itens"
    (is (=  {"a" 1, "b" 1, "v" 1} (count-occurrences '("a" "b" "v")))))
  (testing "Testing count-occurrences for repeated item"
    (is (=  {"a" 3, "b" 1, "v" 1} (count-occurrences '("a" "b" "a" "v" "a")))))
  (testing "Testing count-occurrences for empty list"
    (is (= {} (count-occurrences '())))))

(deftest sort-by-occurrences-test
  (testing "Testing sort-by-occurrences"
    (is (= '(["b" 5] ["a" 3] ["v" 1]) (sort-by-occurrences {"a" 3, "b" 5, "v" 1})))))

(run-tests)


