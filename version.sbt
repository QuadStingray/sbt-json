ThisBuild / version := {
  val json = IO.read(baseDirectory.value / "package.json")
  val versionRegex = """"version"\s*:\s*"([^"]+)"""".r
  versionRegex.findFirstMatchIn(json)
    .map(_.group(1).trim.toLowerCase.replace(".snapshot", "-SNAPSHOT").trim)
    .getOrElse("0.0.1-SNAPSHOT")
}
