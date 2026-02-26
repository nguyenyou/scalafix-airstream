# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Scalafix syntactic lint rule (`NoCombineWith`) that flags `.combineWith()` and `.combineWithFn()` instance methods, suggesting companion object calls (`Signal.combine()`, `EventStream.combine()`) instead.

Published as `io.github.nguyenyou::scalafix-airstream` on Maven Central.

## Build System

Dual build: **sbt for testing**, **Mill for publishing**.

### Testing (sbt)

```sh
sbt tests/test          # run all tests
sbt "tests/testOnly fix.NoCombineWithSuite"  # run single suite
```

### Publishing (Mill)

Version is derived from the nearest `v*` git tag (see `build.mill`). Clear `out/rules/publishVersion.dest` if the cached version is stale.

```sh
git tag v0.3.0
./mill rules.publishSonatypeCentral   # publish to Maven Central
git push origin v0.3.0
./mill rules.publishLocal             # publish locally
./mill show rules.publishVersion      # check resolved version
```

Requires env vars: `MILL_SONATYPE_USERNAME`, `MILL_SONATYPE_PASSWORD`, `MILL_PGP_SECRET_BASE64`, `MILL_PGP_PASSPHRASE`.

## Architecture

- `rules/src/main/scala/fix/NoCombineWith.scala` — the scalafix rule (syntactic lint)
- `rules/src/main/resources/META-INF/services/scalafix.v1.Rule` — ServiceLoader registration
- `input/src/main/scala/fix/` — test input files (with `/* assert: */` lint assertions)
- `tests/src/test/scala/fix/` — scalafix-testkit test suite
- `examples/mill/` — working Mill example project
- `build.sbt` — sbt build (testing with scalafix-testkit)
- `build.mill` — Mill build (publishing with `SonatypeCentralPublishModule`)

## Key Details

- **Syntactic lint rule**: no SemanticDB needed at runtime; matches `Term.Select` AST nodes
- **Lint-only**: reports diagnostics via `Patch.lint(Diagnostic(...))`, no autofix
- **Scala 2.13**: scalafix rules must be compiled with Scala 2.13
- **SemanticDB in tests**: testkit needs `target/scala-2.13/meta/` on classpath (configured in `build.sbt`)
