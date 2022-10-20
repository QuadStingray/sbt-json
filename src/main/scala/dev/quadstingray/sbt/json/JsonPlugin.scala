package dev.quadstingray.sbt.json

import sbt._



object JsonPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = noTrigger

  override val requires: Plugins = plugins.JvmPlugin

  object autoImport {
    lazy val jsonFiles = settingKey[Seq[File]]("List of Json-Files to read from.")
  }

  import autoImport._

  override lazy val projectSettings: Seq[Setting[_]] = Seq(jsonFiles := Seq[File]())
}
