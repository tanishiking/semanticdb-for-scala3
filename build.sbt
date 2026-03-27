ThisBuild / scalafixScalaBinaryVersion :=
  CrossVersion.binaryScalaVersion(scalaVersion.value)

val scalametaVersion = "4.13.9"
val semanticdbProtoURL =
  "https://raw.githubusercontent.com/scalameta/scalameta/refs/tags/v%s/semanticdb/semanticdb/shared/src/main/proto/semanticdb.proto"
    .format(scalametaVersion)

ThisBuild / scalaVersion := "2.13.16"

commands += Command.command("ci") { s =>
  s"check-protobuf" ::
    s"clean" ::
    s"clean-generated" ::
    s"generate" ::
    s
}

commands += Command.command("generate") { s =>
  s"fetch-protobuf" ::
    s"generator/protocGenerate" ::
    s"output/scalafix AdjustForScala3" ::
    s"remove-scalameta-proto" ::
    s"output/compile" ::
    s
}

commands += Command.command("check-protobuf") { s =>
  import scala.sys.process._
  val generatorDir = (generator / Compile / baseDirectory).value
  assert(
    (("%s/src/main/fetch-proto.sh %s %s %s")
      .format(generatorDir, semanticdbProtoURL, generatorDir, "true") !) == 0
  );
  s
}

commands += Command.command("fetch-protobuf") { s =>
  import scala.sys.process._
  val generatorDir = (generator / Compile / baseDirectory).value
  assert(
    (("%s/src/main/fetch-proto.sh %s %s %s")
      .format(generatorDir, semanticdbProtoURL, generatorDir, "false") !) == 0
  );
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
    scalaVersion := "3.1.3",
    Compile / scalafix / unmanagedSources :=
      (Compile / unmanagedSources).value
        .filterNot(file => file.getParent().endsWith("internal"))
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
