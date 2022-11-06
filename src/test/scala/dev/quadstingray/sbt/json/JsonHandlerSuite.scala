package dev.quadstingray.sbt.json

import munit.FunSuite
import org.joda.time.DateTime

class JsonHandlerSuite extends FunSuite {
  val jsonHandler = new JsonHandler(
    List(
      new sbt.File(getClass.getResource("/package.json").getPath),
      new sbt.File(getClass.getResource("/second.json").getPath),
      new sbt.File(getClass.getResource("/second.json").getPath)
    )
  )

  test("read String from simple json values from file") {
    assertEquals(jsonHandler.stringValue("package.json", "version"), "1.2.2.snapshot")
    assertEquals(jsonHandler.stringValue("second.json", "organization"), "dev.quadstingray")
    assertEquals(jsonHandler.stringOption("second.json", "organization"), Some("dev.quadstingray"))
  }

  test("read String List from json values from file") {
    assertEquals(jsonHandler.stringListValue("package.json", "array"), List("hello", "world"))
  }

  test("read double from simple json values from file") {
    assertEquals(jsonHandler.doubleValue("package.json", "number"), 5.0)
    assertEquals(jsonHandler.doubleValue("package.json", "double"), 2.2)
    assertEquals(jsonHandler.doubleOption("package.json", "number"), Some(5.0))
    assertEquals(jsonHandler.doubleOption("package.json", "double"), Some(2.2))
  }

  test("read int from simple json values from file") {
    assertEquals(jsonHandler.intValue("package.json", "number"), 5)
    assertEquals(jsonHandler.intValue("package.json", "double"), 2)
    assertEquals(jsonHandler.intOption("package.json", "number"), Some(5))
    assertEquals(jsonHandler.intOption("package.json", "double"), Some(2))
  }

  test("read long from simple json values from file") {
    assertEquals(jsonHandler.longValue("package.json", "number"), 5L)
    assertEquals(jsonHandler.longValue("package.json", "double"), 2L)
    assertEquals(jsonHandler.longOption("package.json", "number"), Some(5L))
    assertEquals(jsonHandler.longOption("package.json", "double"), Some(2L))
  }

  test("read date from simple json values from file") {
    val testDate = new DateTime("2022-12-01T12:00:00Z").toDate
    assertEquals(jsonHandler.dateValue("package.json", "date"), testDate)
    assertEquals(jsonHandler.dateOption("package.json", "date"), Some(testDate))
    assertEquals(jsonHandler.stringValue("package.json", "date"), "2022-12-01T12:00:00Z")
  }

  test("read String from json values with sub field") {
    assertEquals(jsonHandler.stringValue("package.json", "test.doc"), "Hello")
    assertEquals(jsonHandler.stringValue("package.json", "directories.doc"), "docs")
    assertEquals(jsonHandler.stringValue("package.json", "test.help.me"), "wanted")
    assertEquals(jsonHandler.stringValue("package.json", "hello.world.value"), "hello")
  }

}
