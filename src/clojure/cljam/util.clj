(ns cljam.util
  (:require [clojure.java.io :refer [file]]))

;;; disk cache

(def temp-dir (let [dir-path (.getPath (file (System/getProperty "java.io.tmpdir") "cljam"))]
                (.mkdirs (file dir-path))
                dir-path))

;;; byte array

(defn ubyte
  "Casts to byte avoiding an error about out of range for byte."
  [n]
  {:pre [(<= 0 n 255)]}
  (byte (if (< n 0x80) n (- n 0x100))))

;;; string utils

(def ^:private upper-case-offset (byte (- (byte \A) (byte \a))))

(defn upper-case
  "Converts a lower case letter to upper case."
  [b]
  (if (or (< b (byte \a)) (> b (byte \z)))
    b
    (byte (+ b upper-case-offset))))

(defn string->bytes [^String s]
  (let [buf (byte-array (count s))]
    (.getBytes s 0 (count buf) buf 0)
    buf))

(defn ^String bytes->string [^bytes b]
  (String. b 0 (count b)))

(defn from-hex-digit [^Character c]
  (let [d (Character/digit c 16)]
    (when (= d -1)
      (throw (NumberFormatException. (str "Invalid hex digit: " c))))
    d))

(defn hex-string->bytes [s]
  {:pre [(even? (count s))]}
  (byte-array
   (map #(byte (bit-or (bit-shift-left (from-hex-digit (nth s (* % 2))) 4)
                       from-hex-digit (nth s (inc (* % 2)))))
        (range (count s)))))

;;; seq utils

(defn gen-vec
  ([n]
     (gen-vec n nil))
  ([n ini]
     (vec (repeat n ini))))
