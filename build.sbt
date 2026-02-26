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
    githubWorkflowJavaVersions := Seq(JavaSpec.temurin("17")),
    githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("tests/test"))),
    githubWorkflowTargetTags ++= Seq("v*"),
    githubWorkflowPublishTargetBranches := Seq(RefPredicate.StartsWith(Ref.Tag("v"))),
    githubWorkflowPublish := Seq(
      WorkflowStep.Run(
        List(
          "curl -L https://repo1.maven.org/maven2/com/lihaoyi/mill-dist/1.1.2/mill-dist-1.1.2-mill.sh -o mill",
          "chmod +x mill",
          "./mill rules.publishSonatypeCentral"
        ),
        name = Some("Publish to Maven Central"),
        env = Map(
          "MILL_SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}",
          "MILL_SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
          "MILL_PGP_SECRET_BASE64" -> "${{ secrets.PGP_SECRET_BASE64 }}",
          "MILL_PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}"
        )
      )
    ),
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
