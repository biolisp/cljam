(ns cljam.io.bam.core
  "The core of BAM features."
  (:require [clojure.java.io :as cio]
            [clojure.string :as cstr]
            [cljam.io.bam [common :as common]
             [reader :as reader]
             [writer :as writer]]
            [cljam.io.bam-index :as bai]
            [cljam.io.util.lsb :as lsb]
            [cljam.io.util.pbgzf :as pbgzf]
            [cljam.io.util.bgzf :as bgzf])
  (:import java.util.Arrays
           [java.io DataInputStream DataOutputStream IOException FileNotFoundException]
           [bgzf4j BGZFInputStream BGZFOutputStream]
           cljam.io.bam.reader.BAMReader
           cljam.io.bam.writer.BAMWriter))

;; Reading
;; -------

(defn- bam-index
  "Load an index file (BAI) for the given BAM file."
  [bam-path]
  (if-let [bai-path (->> ["$1.bai" ".bai" "$1.BAI" ".BAI"]
                         (eduction
                          (comp
                           (map #(cstr/replace bam-path #"(?i)(\.bam)$" %))
                           (filter #(.isFile (cio/file %)))))
                         first)]
    (bai/bam-index bai-path)
    (throw (FileNotFoundException. (str "Could not find BAM Index file for " bam-path)))))

(defn ^BAMReader reader
  "Creates a `cljam.io.bam.BAMReader` instance for the given path."
  [f]
  (let [;rdr (BGZFInputStream. (cio/file f))
        rdr (pbgzf/parallel-bgzf-input-stream f 8)
        data-rdr (DataInputStream. rdr)]
    (when-not (Arrays/equals ^bytes (lsb/read-bytes data-rdr 4) (.getBytes ^String common/bam-magic))
      (throw (IOException. "Invalid BAM file")))
    (let [{:keys [header refs]} (reader/load-headers data-rdr)
          index-delay (delay (bam-index f))]
      (BAMReader. (.getAbsolutePath (cio/file f))
                  header refs rdr data-rdr index-delay (bgzf/get-file-pointer rdr)))))

(defn ^BAMReader clone-reader
  "Clones bam reader sharing persistent objects."
  [^BAMReader rdr]
  (let [bgzf-rdr (BGZFInputStream. (cio/file (.f rdr)))
        data-rdr (DataInputStream. bgzf-rdr)]
    (.seek bgzf-rdr (.start-pos rdr))
    (BAMReader. (.f rdr) (.header rdr) (.refs rdr) bgzf-rdr data-rdr (.index-delay rdr) (.start-pos rdr))))

;; Writing
;; -------

(defn ^BAMWriter writer [f]
  (BAMWriter. (.getAbsolutePath (cio/file f))
              (DataOutputStream. (BGZFOutputStream. (cio/file f)))))
