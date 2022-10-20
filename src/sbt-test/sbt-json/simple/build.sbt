import dev.quadstingray.sbt.json.Json
import scala.reflect.io.File

name := "simple-json"

organization := "dev.quadstingray"

version := "0.1"

scalaVersion := "2.12.10"

mainClass := Option("com.quadstingray.json.sample.HelloApp")

jsonFiles += file("package.json")

TaskKey[Unit]("check") := {
  val version = Json.fromJson("package.json", "version")
  if (!"1.2.2.snapshot".equals(version)) {
    throw new Exception(s"readed value is ${version}")
  }
}