# scalafix-airstream

[Scalafix](https://scalacenter.github.io/scalafix/) rules for [Airstream](https://github.com/raquo/Airstream).

## Rules

### `NoCombineWith`

Bans instance methods `.combineWith()` and `.combineWithFn()` on `Signal`. Use the companion object methods `Signal.combine()` / `Signal.combineWithFn()` instead.

```scala
// Bad
val combined = mySignal.combineWith(otherSignal)
val mapped = mySignal.combineWithFn(otherSignal)(_ + _)

// Good
val combined = Signal.combine(mySignal, otherSignal)
val mapped = Signal.combineWithFn(mySignal, otherSignal)(_ + _)
```

This is a syntactic rule, so it does not require SemanticDB.

> **Note:** Because it is syntactic, the rule triggers on _any_ `.combineWith` / `.combineWithFn` call regardless of the receiver type. If you have unrelated types with the same method names, suppress with `// scalafix:ok NoCombineWith`.

## Usage

### Mill (with [`mill-scalafix`](https://github.com/joan38/mill-scalafix))

Add the plugin dependency in `mill-build/build.mill`:

```scala
import mill.*
import mill.meta.MillBuildRootModule
import mill.scalalib.*

object `package` extends MillBuildRootModule {
  override def mvnDeps = Seq(
    mvn"com.goyeau::mill-scalafix_mill1:0.6.0"
  )
}
```

Mix in `ScalafixModule` and declare the rule dependency in `build.mill`:

```scala
package build

import mill._, scalalib._
import com.goyeau.mill.scalafix.ScalafixModule

object app extends ScalaModule with ScalafixModule {
  def scalaVersion = "3.3.7"

  def scalafixIvyDeps = Seq(
    mvn"io.github.nguyenyou::scalafix-airstream:VERSION"
  )
}
```

Add `.scalafix.conf` at the project root:

```hocon
rules = [
  NoCombineWith
]

lint.error = [
  "NoCombineWith.*"
]
```

Run:

```sh
./mill app.fix             # report lint
./mill app.fix --check     # check only (CI)
```

See [`examples/mill/`](examples/mill/) for a complete working example.

### sbt (with `sbt-scalafix`)

**`project/plugins.sbt`**

```scala
addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.14.6")
```

**`build.sbt`**

```scala
ThisBuild / scalafixDependencies += "io.github.nguyenyou" %% "scalafix-airstream" % "VERSION"
```

**`.scalafix.conf`** (same as above)

```hocon
rules = [
  NoCombineWith
]

lint.error = [
  "NoCombineWith.*"
]
```

Run:

```sh
sbt scalafix             # report lint
sbt 'scalafix --check'   # check only (CI)
```

## Development

```sh
sbt tests/test
```
