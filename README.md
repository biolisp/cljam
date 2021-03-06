# cljam

A DNA Sequence Alignment/Map (SAM) library for Clojure. [[API Reference]][api-reference] [[Annotated Source]][annotated-source]

[![Clojars Project](https://img.shields.io/clojars/v/cljam.svg)](https://clojars.org/cljam)

[![Build Status](https://travis-ci.org/chrovis/cljam.svg?branch=master)](https://travis-ci.org/chrovis/cljam)

[![codecov](https://codecov.io/gh/chrovis/cljam/branch/master/graph/badge.svg)](https://codecov.io/gh/chrovis/cljam)

## Installation

cljam is available as a Maven artifact from [Clojars](https://clojars.org/cljam).

To use with Leiningen/Boot, add the following dependency.

```clojure
[cljam "0.5.1"]
```

To use with Maven, add the following dependency.

```xml
<dependency>
  <groupId>cljam</groupId>
  <artifactId>cljam</artifactId>
  <version>0.5.1</version>
</dependency>
```

## Breaking changes in 0.5.0

* `:ignore-index` option of `cljam.io.sam/bam-reader` and `cljam.io.sequence/fasta-reader` is removed.
* `:depth` option of `cljam.io.sam/read-alignments` is removed. It returns `SAMAlignment` only.
* SAM/BAM reading functions return `Eduction` instances instead of lazy sequences.

## Getting started

To read a SAM/BAM format file,

```clojure
(require '[cljam.io.sam :as sam])

;; Open a file
(with-open [r (sam/reader "path/to/file.bam")]
  ;; Retrieve header
  (sam/read-header r)
  ;; Retrieve alignments
  (doall (take 5 (sam/read-alignments r))))
```

To create a sorted file,

```clojure
(require '[cljam.io.sam :as sam]
         '[cljam.algo.sorter :as sorter])

(with-open [r (sam/reader "path/to/file.bam")
            w (sam/writer "path/to/sorted.bam")]
  ;; Sort by chromosomal coordinates
  (sorter/sort-by-pos r w))
```

To create a BAM index file,

```clojure
(require '[cljam.algo.bam-indexer :as bai])

;; Create a new BAM index file
(bai/create-index "path/to/sorted.bam" "path/to/sorted.bam.bai")
```

To calculate coverage depth for a BAM file,

```clojure
(require '[cljam.io.sam :as sam]
         '[cljam.algo.depth :as depth])

(with-open [r (sam/reader "path/to/sorted.bam")]
  ;; Pileup "chr1" alignments
  (depth/depth r {:chr "chr1", :start 1, :end 10}))
;;=> (0 0 0 0 0 0 1 1 3 3)
```

If you are Clojure beginner, read [Getting Started for Clojure Beginners](https://github.com/chrovis/cljam/wiki/Getting-Started-for-Clojure-Beginners).

## Command-line tool

cljam provides a command-line tool to use the features easily.

### Executable installation

`lein bin` creates standalone console executable into `target` directory.

```console
$ lein bin
Creating standalone executable: /path/to/cljam/target/cljam
```

Copy the executable `cljam` somewhere in your `$PATH`.

### Usage

All commands are displayed by `cljam -h`, and detailed help for each command are displayed by `cljam [cmd] -h`.

```console
$ cljam view -h
```

For example, to display contents of a SAM file including the header,

```console
$ cljam view --header path/to/file.sam
```

See [command-line tool manual](https://github.com/chrovis/cljam/wiki/Command-line-tool) for more information.

## Development

### Test

To run tests,

- `lein test` for basic tests,
- `lein test :slow` for slow tests with local resources,
- `lein test :remote` for tests with remote resources.

To get coverage

```console
$ lein cloverage
```

And open `target/coverage/index.html`.

### Generating document

cljam uses [Codox](https://github.com/weavejester/codox) for API reference and
[Marginalia](https://github.com/gdeer81/marginalia) for annotated source code.

```console
$ lein docs
```

generates these documents in `target/docs` and `target/literate` directories.

## Citing cljam

T. Takeuchi, A. Yamada, T. Aoki, and K. Nishimura. [cljam: a library for handling DNA sequence alignment/map (SAM) with parallel processing](http://dx.doi.org/10.1186/s13029-016-0058-6). Source Code for Biology and Medicine, Vol. 11, No. 1, pp. 1-4, 2016.

## Contributors

Sorted by first commit.

- Toshiki Takeuchi ([@totakke](https://github.com/totakke))
- Takashi Aoki ([@federkasten](https://github.com/federkasten))
- Atsuo Yamada ([@ayamada](https://github.com/ayamada))
- Jun Imura ([@alumi](https://github.com/alumi))

## License

Copyright 2013-2017 [Xcoo, Inc.](https://xcoo.jp/)

Licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).

[api-reference]: https://chrovis.github.io/cljam/docs
[annotated-source]: https://chrovis.github.io/cljam/literate
