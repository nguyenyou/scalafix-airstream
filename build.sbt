lazy val V = new {
  val scalafixVersion = _root_.scalafix.sbt.BuildInfo.scalafixVersion
  val scala213 = "2.13.18"
}

inThisBuild(
  List(
    organization := "io.github.nguyenyou",
    scalaVersion := V.scala213,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
  )
)

lazy val rules = project.settings(
  moduleName := "scalafix-airstream",
  libraryDependencies += "ch.epfl.scala" %% "scalafix-core" % V.scalafixVersion,
)

lazy val input = project.settings(
  publish / skip := true,
)

lazy val output = project.settings(
  publish / skip := true,
)

lazy val tests = project
  .settings(
    publish / skip := true,
    libraryDependencies += "ch.epfl.scala" % "scalafix-testkit" % V.scalafixVersion % Test cross CrossVersion.full,
    scalafixTestkitOutputSourceDirectories := (output / Compile / unmanagedSourceDirectories).value,
    scalafixTestkitInputSourceDirectories := (input / Compile / unmanagedSourceDirectories).value,
    scalafixTestkitInputClasspath := {
      val cp = (input / Compile / fullClasspath).value
      val metaDir = (input / Compile / classDirectory).value.toPath.getParent.resolve("meta").toFile
      cp :+ Attributed.blank(metaDir)
    },
  )
  .dependsOn(rules)
  .enablePlugins(ScalafixTestkitPlugin)
