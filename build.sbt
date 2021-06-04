ThisBuild / scalafixScalaBinaryVersion :=
  CrossVersion.binaryScalaVersion(scalaVersion.value)

ThisBuild / scalaVersion := "2.13.6"

commands += Command.command("generate") { s =>
  s"original/compile" ::
    s"original/scalafix AdjustForScala3" ::
    s"remove-scalameta-proto" ::
    s
}
commands += Command.command("clean-generated") { s =>
  IO.delete(
    (original / Compile / baseDirectory).value / "src" / "main" / "scala" / "generated"
  )
  s
}

commands += Command.command("remove-scalameta-proto") { s =>
  IO.delete(
    (original / Compile / baseDirectory).value / "src" / "main" / "scala" / "generated" / "dotty" / "tools" / "dotc" / "semanticdb" / "ScalametaProto.scala"
  )
  s
}

lazy val original = project
  .in(file("original"))
  .settings(
    name := "original",
    // (optional) If you need scalapb/scalapb.proto or anything from
    // google/protobuf/*.proto
    libraryDependencies ++= Seq(
      "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
    ),
    Compile / PB.targets := Seq(
      scalapb
        .gen(
          lenses = false,
          flatPackage = true
        ) -> (Compile / baseDirectory).value / "src" / "main" / "scala" / "generated"
    )
  )
  .dependsOn(`scalafix-rules` % ScalafixConfig)

lazy val `scalafix-rules` = (project in file("scalafix/rules"))
  .disablePlugins(ScalafixPlugin)
  .settings(
    name := "scalafix",
    libraryDependencies +=
      "ch.epfl.scala" %%
        "scalafix-core" %
        _root_.scalafix.sbt.BuildInfo.scalafixVersion
  )
