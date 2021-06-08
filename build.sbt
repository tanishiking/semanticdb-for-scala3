ThisBuild / scalafixScalaBinaryVersion :=
  CrossVersion.binaryScalaVersion(scalaVersion.value)

ThisBuild / scalaVersion := "2.13.6"

commands += Command.command("generate") { s =>
  s"generator/protocGenerate" ::
    s"output/scalafix AdjustForScala3" ::
    s"remove-scalameta-proto" ::
    s"output/compile" ::
    s
}
commands += Command.command("clean-generated") { s =>
  IO.delete(
    (output / Compile / baseDirectory).value / "src" / "main" / "scala" / "generated"
  )
  s
}

commands += Command.command("remove-scalameta-proto") { s =>
  IO.delete(
    (output / Compile / baseDirectory).value / "src" / "main" / "scala" / "generated" / "dotty" / "tools" / "dotc" / "semanticdb" / "ScalametaProto.scala"
  )
  s
}

lazy val output = project
  .in(file("output"))
  .settings(
    name := "output",
    scalaVersion := "3.0.0"
  )
  .dependsOn(`scalafix-rules` % ScalafixConfig)

lazy val generator = project
  .in(file("generator"))
  .settings(
    name := "generator",
    libraryDependencies ++= Seq(
      "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
    ),
    Compile / PB.targets := Seq(
      scalapb
        .gen(
          lenses = false,
          flatPackage = true
        ) -> (Compile / baseDirectory).value / ".." / "output" / "src" / "main" / "scala" / "generated"
    )
  )

lazy val `scalafix-rules` = (project in file("scalafix/rules"))
  .disablePlugins(ScalafixPlugin)
  .settings(
    name := "scalafix",
    libraryDependencies +=
      "ch.epfl.scala" %%
        "scalafix-core" %
        _root_.scalafix.sbt.BuildInfo.scalafixVersion
  )
