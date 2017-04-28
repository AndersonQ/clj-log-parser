(ns log-parser.core
  (:require [clojure.string :as str]))

(def log-txt (slurp "log-test.txt"))

(defn load-lines [file]
  (str/split-lines (slurp file)))

(defn clean-lines [lines]
  (filter #(= 2 (count %))
          (map #(re-seq #"request_to=\"[\w:\/\.]*\"|response_status=\"\d{3}\"" %)
               lines)))

(defn extract-info [lines]
  (map #(second (clojure.string/split % #"=")) lines))

(defn remove-quotes [lines]
  (map #(str/replace % #"\"" "") lines))

;; (defn parse-lines [file]
;;   (-> file
;;       load-lines
;;       clean-lines
;;       flatten
;;       extract-info
;;       remove-quotes
;;       assembly))

(defn filter-response-codes [lines]
  (filter #(re-matches #"\d{3}" %) lines))

(defn filter-urls [lines]
  (filter #(not (re-matches #"\d{3}" %)) lines))

(defn count-occurrences [strs]
  (loop [items strs occurrences {}]
    (if (empty? items)
      occurrences
      (let [item (first items)
            count (get occurrences item 0)]
        (recur (rest items) (merge occurrences {item (inc count)}))))))

(defn sort-by-occurrences [items]
  (sort-by val > items))

(defn assembly [lines]
  (let [urls (filter-urls lines)
        codes (filter-response-codes lines)]
    {:urls (-> urls
               count-occurrences
               sort-by-occurrences)
     :codes (-> codes
                 count-occurrences
                 sort-by-occurrences)}))

;; (defn sort-by-occurrences [items]
;;   (sort-by val > items))

(defn parse-lines [file]
  (-> file
      load-lines
      clean-lines
      flatten
      extract-info
      remove-quotes
      assembly))


(defn -main [file]
  (let [result (parse-lines file)
        top-3-urls (take 3 (:urls result))
        top-3-codes (take 3 (:codes result))]
    
     (println "Most frequent urls:")
     (println (reduce #(println-str %1 (first %2) ": " (second %2)) "" top-3-urls))
     (println "Most frequent return codes: ")
     (println (reduce #(println-str %1 (first %2) ": " (second %2)) "" top-3-codes))))

;(-main "log-test.txt")

