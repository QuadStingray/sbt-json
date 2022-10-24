import dev.quadstingray.sbt.json.Json

name := "simple-json"

val json = Json(file("package.json"))

organization := json.stringValue("organization")

version := json.stringValue("version")

scalaVersion := "2.12.10"

mainClass := Option("com.quadstingray.json.sample.HelloApp")

TaskKey[Unit]("check") := {
  val jsonVersion = json.stringValue("version")
  if (!"1.2.2.snapshot".equals(jsonVersion)) {
    throw new Exception(s"json read version value is ${jsonVersion}")
  }
  val jsonOrganization = json.stringValue("organization")
  if (!"dev.quadstingray".equals(jsonOrganization)) {
    throw new Exception(s"json read organization value is ${jsonOrganization}")
  }
  if (!"1.2.2.snapshot".equals(version.value)) {
    throw new Exception(s"version is: ${version.value}")
  }
  if (!"dev.quadstingray".equals(organization.value)) {
    throw new Exception(s"organization is: ${organization.value}")
  }
}