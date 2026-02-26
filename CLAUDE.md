# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Scalafix syntactic lint rule (`NoCombineWith`) that bans `.combineWith()` and `.combineWithFn()` instance methods on `Signal` and `EventStream`, preferring companion object calls (`Signal.combine()`, `EventStream.combine()`, etc.).

Published as `io.github.nguyenyou::scalafix-airstream` on Maven Central.

## Build System

Dual build: **sbt for testing**, **Mill for publishing**.

### Testing (sbt)

```sh
sbt tests/test          # run all tests
sbt "tests/testOnly fix.NoCombineWithSuite"  # run single suite
```

### Publishing (Mill)

Version is derived from the nearest `v*` git tag (see `build.mill`).

```sh
./mill rules.publishSonatypeCentral   # publish to Maven Central
./mill rules.publishLocal             # publish locally
./mill show rules.publishVersion      # check resolved version
```

Requires env vars: `MILL_SONATYPE_USERNAME`, `MILL_SONATYPE_PASSWORD`, `MILL_PGP_SECRET_BASE64`, `MILL_PGP_PASSPHRASE`.

## Architecture

- `rules/src/main/scala/fix/` — the scalafix rule (`NoCombineWith.scala`)
- `rules/src/main/resources/META-INF/services/scalafix.v1.Rule` — ServiceLoader registration
- `input/src/main/scala/fix/` — test input files with `/* assert: NoCombineWith */` lint assertions
- `tests/src/test/scala/fix/` — scalafix-testkit test suite
- `output/` — empty (lint-only rule, no rewrite output needed)
- `examples/mill/` — working Mill example project
- `build.sbt` — sbt build (testing with scalafix-testkit)
- `build.mill` — Mill build (publishing with `SonatypeCentralPublishModule`)
- `project/plugins.sbt` — sbt plugins (sbt-scalafix)

## Key Details

- **Syntactic rule**: no SemanticDB needed at runtime; pattern matches `Term.Select` AST nodes
- **Scala 2.13**: scalafix rules must be compiled with Scala 2.13
- **SemanticDB in tests**: testkit needs `target/scala-2.13/meta/` on classpath (configured in `build.sbt`)
- **Lint-only**: no output file needed for test cases — testkit auto-passes diff when output dir is empty
