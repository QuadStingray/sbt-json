name := "simple-json"

organization := jsonHandler.value.stringValue("second.json", "organization")

version := jsonHandler.value.stringValue("package.json", "version")

scalaVersion := "2.12.10"

mainClass := Option("com.quadstingray.json.sample.HelloApp")

jsonFiles += (baseDirectory.value / "second.json")
jsonFiles += (baseDirectory.value / "package.json")

TaskKey[Unit]("check") := {
  val jsonVersion = jsonHandler.value.stringValue("package.json", "version")
  if (!"1.2.2.snapshot".equals(jsonVersion)) {
    throw new Exception(s"json read version value is ${jsonVersion}")
  }
  val jsonOrganization = jsonHandler.value.stringValue("second.json", "organization")
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