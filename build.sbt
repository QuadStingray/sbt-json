name := "sbt-json"

organization := "dev.quadstingray"

homepage := Some(uri("https://quadstingray.github.io/sbt-json/"))

scmInfo := Some(ScmInfo(uri("https://github.com/QuadStingray/sbt-json"), "https://github.com/QuadStingray/sbt-json.git"))

developers := List(Developer("QuadStingray", "QuadStingray", "github@quadstingray.dev", uri("https://github.com/QuadStingray")))

licenses += ("Apache-2.0", uri("https://github.com/QuadStingray/sbt-json/blob/master/LICENSE"))

description := "sbt plugin for to load properties in sbt process from json files."

publishMavenStyle := true

crossSbtVersions := Vector("1.12.14", "2.0.3")

pluginCrossBuild / sbtVersion := (Global / pluginCrossBuild / sbtVersion).?.value.getOrElse(sbtVersion.value)

initialCommands := "import dev.quadstingray.sbt.json._"

initialCommands := "import dev.quadstingray.sbt.json.Json._"

initialCommands := "import dev.quadstingray.sbt.json.JsonPlugin.autoImport._"

initialCommands := "import dev.quadstingray.sbt.json.JsonPlugin._"

libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.9.2"

val circeVersion = "0.14.15"

libraryDependencies += "io.circe" %% "circe-core" % circeVersion

libraryDependencies += "io.circe" %% "circe-parser" % circeVersion

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.6"

libraryDependencies += "joda-time" % "joda-time" % "2.14.2"

libraryDependencies += "org.scalameta" %% "munit" % "1.3.3" % Test
