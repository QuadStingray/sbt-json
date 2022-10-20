
name := "sbt-json"

organization := "dev.quadstingray"

homepage := Some(url("https://quadstingray.github.io/sbt-json/"))

scmInfo := Some(ScmInfo(url("https://github.com/QuadStingray/sbt-json"), "https://github.com/QuadStingray/sbt-json.git"))

developers := List(Developer("QuadStingray", "QuadStingray", "github@quadstingray.dev", url("https://github.com/QuadStingray")))

licenses += ("Apache-2.0", url("https://github.com/QuadStingray/sbt-json/blob/master/LICENSE"))

description := "sbt plugin for to load properties in sbt process from json files."

publishMavenStyle := false

scalaVersion := crossScalaVersions.value.last

crossScalaVersions := List("2.12.1")

crossSbtVersions := Vector("1.7.2")

console / initialCommands := "import dev.quadstingray.sbt.json._"

// libraryDependencies += "com.sfxcode.sapphire" % "sapphire-data" % "2.0.0"

libraryDependencies += "org.json4s" %% "json4s-native" % "4.0.6"


