package dev.quadstingray.sbt.json

import munit.FunSuite
import org.joda.time.DateTime

class JsonSuite extends FunSuite {
  val json: JsonFile = JsonFile(new sbt.File(getClass.getResource("/package.json").getPath))

  test("read String from simple json values from file") {
    assertEquals(json.stringValue("version"), "1.2.2.snapshot")
    assertEquals(json.stringOption("version"), Some("1.2.2.snapshot"))
  }

  test("read String List from json values from file") {
    assertEquals(json.stringListValue("array"), List("hello", "world"))
  }

  test("read double from simple json values from file") {
    assertEquals(json.doubleValue("number"), 5.0)
    assertEquals(json.doubleValue("double"), 2.2)
    assertEquals(json.doubleOption("number"), Some(5.0))
    assertEquals(json.doubleOption("double"), Some(2.2))
  }

  test("read int from simple json values from file") {
    assertEquals(json.intValue("number"), 5)
    assertEquals(json.intValue("double"), 2)
    assertEquals(json.intOption("number"), Some(5))
    assertEquals(json.intOption("double"), Some(2))
  }

  test("read long from simple json values from file") {
    assertEquals(json.longValue("number"), 5L)
    assertEquals(json.longValue("double"), 2L)
    assertEquals(json.longOption("number"), Some(5L))
    assertEquals(json.longOption("double"), Some(2L))
  }

  test("read date from simple json values from file") {
    val testDate = new DateTime("2022-12-01T12:00:00Z").toDate
    assertEquals(json.dateValue("date"), testDate)
    assertEquals(json.dateOption("date"), Some(testDate))
    assertEquals(json.stringValue("date"), "2022-12-01T12:00:00Z")
  }

  test("read String from json values with sub field") {
    assertEquals(json.stringValue("test.doc"), "Hello")
    assertEquals(json.stringValue("directories.doc"), "docs")
    assertEquals(json.stringValue("test.help.me"), "wanted")
    assertEquals(json.stringValue("hello.world.value"), "hello")
  }

  test("read value from not existing json field") {
    val notExistingKey = "does_not_exists"
    intercept[NoSuchElementException] {
      json.value(notExistingKey)
    }
    intercept[NoSuchElementException] {
      json.dateValue(notExistingKey)
    }
    intercept[NoSuchElementException] {
      json.stringValue(notExistingKey)
    }
    intercept[NoSuchElementException] {
      json.longValue(notExistingKey)
    }
    assertEquals(json.stringOption(notExistingKey), None)
    assertEquals(json.longOption(notExistingKey), None)
    assertEquals(json.dateOption(notExistingKey), None)
    assertEquals(json.valueOption(notExistingKey), None)
  }

  test("update values") {
    val keyForUpdate = "keyForUpdate"
    intercept[NoSuchElementException] {
      json.stringValue(keyForUpdate)
    }
    json.updateValue(keyForUpdate, "hello my")
    assertEquals(json.stringValue(keyForUpdate), "hello my")
    assertEquals(json.stringValue("version"), "1.2.2.snapshot")

    json.updateValue(keyForUpdate, "hello you")
    assertEquals(json.stringValue(keyForUpdate), "hello you")

  }

}
