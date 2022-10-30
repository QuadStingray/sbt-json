name := "sbt-json"

organization := "dev.quadstingray"

homepage := Some(url("https://quadstingray.github.io/sbt-json/"))

scmInfo := Some(ScmInfo(url("https://github.com/QuadStingray/sbt-json"), "https://github.com/QuadStingray/sbt-json.git"))

developers := List(Developer("QuadStingray", "QuadStingray", "github@quadstingray.dev", url("https://github.com/QuadStingray")))

licenses += ("Apache-2.0", url("https://github.com/QuadStingray/sbt-json/blob/master/LICENSE"))

description := "sbt plugin for to load properties in sbt process from json files."

publishMavenStyle := true

scalaVersion := crossScalaVersions.value.last

crossScalaVersions := List("2.13.10")

crossSbtVersions := Vector("1.7.2")

initialCommands := "import dev.quadstingray.sbt.json._"

initialCommands := "import dev.quadstingray.sbt.json.Json._"

initialCommands := "import dev.quadstingray.sbt.json.JsonPlugin.autoImport._"

initialCommands := "import dev.quadstingray.sbt.json.JsonPlugin._"

libraryDependencies += "com.github.pathikrit" %% "better-files" % "3.9.1"

val circeVersion = "0.14.3"

libraryDependencies += "io.circe" %% "circe-core" % circeVersion

libraryDependencies += "io.circe" %% "circe-parser" % circeVersion

libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"

libraryDependencies += "joda-time" % "joda-time" % "2.12.0"

libraryDependencies += "org.scalameta" %% "munit" % "0.7.29" % Test

jsonFiles += (baseDirectory.value / "package.json")
