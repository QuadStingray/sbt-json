package dev.quadstingray.sbt.json

import munit.FunSuite
import org.joda.time.DateTime

class JsonHandlerSuite extends FunSuite {
  val json = new JsonHandler(List(new sbt.File(getClass.getResource("/package.json").getPath), new sbt.File(getClass.getResource("/second.json").getPath), new sbt.File(getClass.getResource("/second.json").getPath)))

  test("read String from simple json values from file") {
    assertEquals(json.stringValue("package.json", "version"), "1.2.2.snapshot")
    assertEquals(json.stringValue("second.json", "organization"), "dev.quadstingray")
    assertEquals(json.stringOption("second.json", "organization"), Some("dev.quadstingray"))
  }

  test("read String List from json values from file") {
    assertEquals(json.stringListValue("package.json", "array"), List("hello", "world"))
  }

  test("read double from simple json values from file") {
    assertEquals(json.doubleValue("package.json", "number"), 5.0)
    assertEquals(json.doubleValue("package.json", "double"), 2.2)
    assertEquals(json.doubleOption("package.json", "number"), Some(5.0))
    assertEquals(json.doubleOption("package.json", "double"), Some(2.2))
  }

  test("read int from simple json values from file") {
    assertEquals(json.intValue("package.json", "number"), 5)
    assertEquals(json.intValue("package.json", "double"), 2)
    assertEquals(json.intOption("package.json", "number"), Some(5))
    assertEquals(json.intOption("package.json", "double"), Some(2))
  }

  test("read long from simple json values from file") {
    assertEquals(json.longValue("package.json", "number"), 5L)
    assertEquals(json.longValue("package.json", "double"), 2L)
    assertEquals(json.longOption("package.json", "number"), Some(5L))
    assertEquals(json.longOption("package.json", "double"), Some(2L))
  }

  test("read date from simple json values from file") {
    val testDate = new DateTime("2022-12-01T12:00:00Z").toDate
    assertEquals(json.dateValue("package.json", "date"), testDate)
    assertEquals(json.dateOption("package.json", "date"), Some(testDate))
    assertEquals(json.stringValue("package.json", "date"), "2022-12-01T12:00:00Z")
  }

  test("read String from json values with sub field") {
    assertEquals(json.stringValue("package.json", "test.doc"), "Hello")
    assertEquals(json.stringValue("package.json", "directories.doc"), "docs")
    assertEquals(json.stringValue("package.json", "test.help.me"), "wanted")
    assertEquals(json.stringValue("package.json", "hello.world.value"), "hello")
  }

}
