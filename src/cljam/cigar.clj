(ns cljam.cigar)

(defn parse
  "Parses CIGAR text, returns seq of lengths and operations."
  [s]
  (for [[_ n op] (re-seq #"([0-9]*)([MIDNSHP=X])" s)]
    [(Integer/parseInt n) (first op)]))

(defn count-op
  "Returns length of CIGAR operations."
  [s]
  (count (parse s)))

(defn count-ref
  "Returns length of reference bases."
  [s]
  (->> (parse s)
       (filter (comp #{\M \D \N \= \X} last))
       (map first)
       (reduce +)))