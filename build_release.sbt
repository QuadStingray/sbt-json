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

// temporary: replicates sbt-release's checkUpstream/isBehindRemote git calls one by one, with full
// exit code + stdout + stderr, to find which one fails silently inside the real release process.
commands += Command.command("diagnose-vcs")((state: State) => {
  def run(args: String*): (Int, String, String) = {
    val out    = new StringBuilder
    val err    = new StringBuilder
    val logger = ProcessLogger(o => { out.append(o); out.append("\n"); }, e => { err.append(e); err.append("\n"); })
    val code   = Process("git" +: args, file(".")) ! logger
    (code, out.toString.trim, err.toString.trim)
  }
  def report(label: String, args: String*): (Int, String, String) = {
    val (code, out, err) = run(args*)
    state.log.info(s"[diagnose-vcs] $label -> exit=$code out=[$out] err=[$err]")
    (code, out, err)
  }

  val (_, branchOut, _)  = report("symbolic-ref HEAD", "symbolic-ref", "HEAD")
  val currentBranch      = branchOut.stripPrefix("refs/heads/")
  val (_, remoteOut, _)  = report(s"config branch.$currentBranch.remote", "config", s"branch.$currentBranch.remote")
  val (_, mergeOut, _)   = report(s"config branch.$currentBranch.merge", "config", s"branch.$currentBranch.merge")
  val trackingBranch     = mergeOut.stripPrefix("refs/heads/")
  report("fetch " + remoteOut, "fetch", remoteOut)
  report(
    s"rev-list $currentBranch..$remoteOut/$trackingBranch",
    "rev-list",
    s"$currentBranch..$remoteOut/$trackingBranch"
  )
  state
})

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
