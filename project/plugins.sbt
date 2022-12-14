libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value

addSbtPlugin("com.github.sbt" % "sbt-release" % "1.1.0")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "3.9.15")

addSbtPlugin("com.github.sbt" % "sbt-pgp" % "2.2.1")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.5.0")

addSbtPlugin("dev.quadstingray" %% "sbt-json" % "0.6.3")
