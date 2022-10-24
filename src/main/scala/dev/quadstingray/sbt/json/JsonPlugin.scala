package dev.quadstingray.sbt.json

import dev.quadstingray.sbt.json.JsonPlugin.autoImport.{jsonFiles, jsonHandler}
import sbt.Keys.{baseDirectory, sLog}
import sbt.{AutoPlugin, Def, *}


object JsonPlugin extends AutoPlugin {
  override val trigger: PluginTrigger = allRequirements

  override val requires: Plugins = plugins.JvmPlugin

  object autoImport {
    lazy val jsonHandler = settingKey[JsonHandler]("JsonHandler for Usage in sbt-Files")
    lazy val jsonFiles = settingKey[Seq[File]]("List of Json-Files to read from.")
  }


  override val projectSettings: Seq[Def.Setting[_]] = {
    Seq(
      jsonFiles := Seq(baseDirectory.value / "package.json"),
      jsonHandler := {
        JsonLogger.sbtLogger = sLog.value
        new JsonHandler(jsonFiles.value)
      }
    )
  }

}
