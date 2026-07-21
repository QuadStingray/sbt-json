package dev.quadstingray.sbt.json

import munit.FunSuite
import java.io.File
import java.nio.file.{Files, StandardCopyOption}

class JsonWriteSuite extends FunSuite with TestResourceHelper {

  var targetFile: File = null.asInstanceOf[File]

  override def beforeEach(context: BeforeEach): Unit = {
    val source = resourceFile("/package.json")
    targetFile = File.createTempFile("package-", ".json")
    targetFile.deleteOnExit()
    Files.copy(source.toPath, targetFile.toPath, StandardCopyOption.REPLACE_EXISTING)
  }

  test("read String from simple json values from file") {
    val json: JsonFile = JsonFile(targetFile)
    assertEquals(json.stringValue("version"), "1.2.2.snapshot")
    json.updateValue("version", "2.0.0")
    json.write()
    assertEquals(json.stringValue("version"), "2.0.0")
    val json2: JsonFile = JsonFile(targetFile)
    assertEquals(json2.stringValue("version"), "2.0.0")
  }

  test("read String from json values with sub field") {
    val json: JsonFile = JsonFile(targetFile)
    assertEquals(json.stringValue("test.doc"), "Hello")
    assertEquals(json.stringValue("directories.doc"), "docs")
    assertEquals(json.stringValue("test.help.me"), "wanted")
    assertEquals(json.stringValue("hello.world.value"), "hello")
    json.updateValue("test.doc", "you see")
    json.updateValue("directories.doc", "your dir")
    json.updateValue("test.help.me", "help is here")
    json.updateValue("hello.world.value", "you see")
    json.write()
    assertEquals(json.stringValue("test.doc"), "you see")
    assertEquals(json.stringValue("directories.doc"), "your dir")
    assertEquals(json.stringValue("test.help.me"), "help is here")
    assertEquals(json.stringValue("hello.world.value"), "you see")
    val json2: JsonFile = JsonFile(targetFile)
    assertEquals(json2.stringValue("test.doc"), "you see")
    assertEquals(json2.stringValue("directories.doc"), "your dir")
    assertEquals(json2.stringValue("test.help.me"), "help is here")
    assertEquals(json2.stringValue("hello.world.value"), "you see")
  }

}
