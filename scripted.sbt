sbtPlugin := true

enablePlugins(SbtPlugin)

scriptedLaunchOpts := {
  val sbtVersionForTests = scriptedSbt.value
  (sbtTestDirectory.value ** "build.properties").get().filter(_.getParentFile.getName == "project").foreach { buildProperties =>
    IO.write(buildProperties, s"sbt.version=$sbtVersionForTests")
  }
  scriptedLaunchOpts.value ++
    Seq("-Dplugin.version=" + version.value)
}

scriptedBufferLog := false