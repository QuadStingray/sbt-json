package dev.quadstingray.sbt.json

import better.files.File
import munit.FunSuite

class JsonWriteSuite extends FunSuite {

  val targetFolder: File = {
    val resourceFile = getClass.getResource("/package.json").getPath
    better.files.File(s"$resourceFile/../../../../../target/test-files")
  }

  override def beforeEach(context: BeforeEach): Unit = {
    val fileToDelete = better.files.File(s"$targetFolder/package.json")
    if (fileToDelete.exists) {
      fileToDelete.delete()
    }
    val resourceFile = getClass.getResource("/package.json").getPath
    val sourceFile   = better.files.File(resourceFile)
    targetFolder.createDirectoryIfNotExists(createParents = true)
    val targetFile = sourceFile.copyToDirectory(targetFolder)
    targetFile
  }

  test("read String from simple json values from file") {
    val json: JsonFile = JsonFile(new sbt.File(s"$targetFolder/package.json"))
    assertEquals(json.stringValue("version"), "1.2.2.snapshot")
    json.updateValue("version", "2.0.0")
    json.write()
    assertEquals(json.stringValue("version"), "2.0.0")
    val json2: JsonFile = JsonFile(new sbt.File(s"$targetFolder/package.json"))
    assertEquals(json2.stringValue("version"), "2.0.0")
  }

  test("read String from json values with sub field") {
    val json: JsonFile = JsonFile(new sbt.File(s"$targetFolder/package.json"))
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
    val json2: JsonFile = JsonFile(new sbt.File(s"$targetFolder/package.json"))
    assertEquals(json2.stringValue("test.doc"), "you see")
    assertEquals(json2.stringValue("directories.doc"), "your dir")
    assertEquals(json2.stringValue("test.help.me"), "help is here")
    assertEquals(json2.stringValue("hello.world.value"), "you see")
  }

}
