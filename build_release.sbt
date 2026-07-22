import sbtrelease.ReleasePlugin.autoImport.ReleaseKeys.versions
import sbtrelease.ReleasePlugin.autoImport.ReleaseTransformations._
import sbtrelease.ReleasePlugin.runtimeVersion

import scala.sys.process._

val gitAddAllTask = ReleaseStep(action = st => {
  "git add .".!
  st
})

val setToMyNextVersion = ReleaseStep(action = st => {
  setMyVersion(st.get(versions).get._2, st)
  st
})

val setToMyReleaseVersion = ReleaseStep(action = st => {
  setMyVersion(st.get(versions).get._1, st)
  st
})

def setMyVersion(version: String, state: State): Unit = {
  state.log.warn(s"Set Version in package.json  to $version")
  val packageJson = file("package.json")
  val newVersion  = version.replace("-SNAPSHOT", ".snapshot")
  val content     = IO.read(packageJson)
  val updated     = content.replaceAll(""""version"\s*:\s*"[^"]+"""", s""""version": "$newVersion"""")
  IO.write(packageJson, updated)
}

releaseNextCommitMessage := s"ci: update version after release"
releaseCommitMessage     := s"ci: prepare release of version ${runtimeVersion.value}"

// Our own version comes from package.json, not sbt-release's own version.sbt, so the repo's version
// is never literally "-SNAPSHOT"-suffixed the way sbt-release's default Bump.Next expects (it checks
// version.isSnapshot and aborts with "Expected snapshot version" otherwise). Bump.NextStable skips
// that check and just strips any qualifier for the release version, matching the mongodb-driver /
// quartz-manager convention of pushing a bare "X.Y.Z" in package.json to trigger a real release.
releaseVersionBump := sbtrelease.Version.Bump.NextStable

commands += Command.command("ci-release")((state: State) => {
  val lowerCaseVersion = version.value.toLowerCase
  if (
    (lowerCaseVersion.contains("snapshot") ||
    lowerCaseVersion.contains("beta") ||
    lowerCaseVersion.contains("rc") ||
    lowerCaseVersion.contains("m"))
  ) {
    state
  }
  else {
    Command.process("release with-defaults", state, _ => ())
  }
})

releaseProcess := {
  Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    runClean,
    setToMyReleaseVersion,
    releaseStepCommand("scalafmt"),
    gitAddAllTask,
    commitReleaseVersion,
    tagRelease,
    releaseStepCommandAndRemaining("+publishSigned"),
    releaseStepCommand("sonaRelease"),
    setToMyNextVersion,
    gitAddAllTask,
    commitNextVersion,
    pushChanges
  )
}

Global / useGpgPinentry := true

// Sonatype sunset the legacy OSSRH staging host in 2025; sonaRelease/localStaging are sbt's own
// native replacement for publishing to the Central Portal (no sbt-sonatype/sbt-ci-release needed).
// Snapshots publish straight to the Central Portal snapshot repo; releases go through local staging
// (target/sona-staging) and get finalized by the sonaRelease step in releaseProcess above.
ThisBuild / publishTo := {
  if (isSnapshot.value) Some("central-snapshots".at("https://central.sonatype.com/repository/maven-snapshots/"))
  else localStaging.value
}

credentials += Credentials("central.sonatype.com", "central.sonatype.com", System.getenv("SONATYPE_USER"), System.getenv("SONATYPE_PASSWORD"))

packageOptions += {
  Package.ManifestAttributes(
    "Created-By"               -> "Simple Build Tool",
    "Built-By"                 -> "QuadStingray",
    "Build-Jdk"                -> System.getProperty("java.version"),
    "Specification-Title"      -> name.value,
    "Specification-Version"    -> version.value,
    "Specification-Vendor"     -> organization.value,
    "Implementation-Title"     -> name.value,
    "Implementation-Version"   -> version.value,
    "Implementation-Vendor-Id" -> organization.value,
    "Implementation-Vendor"    -> organization.value
  )
}
