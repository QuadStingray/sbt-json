package dev.quadstingray.sbt.json

import better.files.File
import com.typesafe.scalalogging.LazyLogging

import java.io.FileNotFoundException
import java.util.Date
import scala.collection.mutable

class JsonHandler(files: Seq[sbt.File]) extends LazyLogging {

  private lazy val internalMap = mutable.Map[String, JsonFile]()

  files.foreach(registerJson)

  def registerJson(file: sbt.File): Unit = {
    val bFile = File(file.toURI)
    try {
      val key = bFile.name
      if (internalMap.contains(key)) {
        JsonLogger.warn(s"$key loaded twice last loaded file: ${bFile.toString()}")
      }
      internalMap.put(key, JsonFile(file))
    } catch {
      case e: Exception =>
        JsonLogger.error(s"could not read Map from ${file.toURI} with error ${e.getMessage}")
    }
  }

  def value(fileName: String, key: String): Any = {
    val json: JsonFile = getRegisteredJson(fileName)
    json.value(key)
  }

  def valueOption(fileName: String, key: String): Option[Any] = {
    val json: JsonFile = getRegisteredJson(fileName)
    json.valueOption(key)
  }

  def stringValue(fileName: String, key: String): String = {
    val json: JsonFile = getRegisteredJson(fileName)
    json.stringValue(key)
  }

  def stringOption(fileName: String, key: String): Option[String] = {
    val json: JsonFile = getRegisteredJson(fileName)
    json.stringOption(key)
  }

  def stringListValue(fileName: String, key: String): List[String] = {
    val json: JsonFile = getRegisteredJson(fileName)
    json.stringListValue(key)
  }

  def longValue(fileName: String, key: String): Long = {
    val json: JsonFile = getRegisteredJson(fileName)
    json.longValue(key)
  }

  def longOption(fileName: String, key: String): Option[Long] = {
    val json: JsonFile = getRegisteredJson(fileName)
    json.longOption(key)
  }

  def intValue(fileName: String, key: String): Int = {
    val json: JsonFile = getRegisteredJson(fileName)
    json.intValue(key)
  }

  def intOption(fileName: String, key: String): Option[Int] = {
    val json: JsonFile = getRegisteredJson(fileName)
    json.intOption(key)
  }

  def dateValue(fileName: String, key: String): Date = {
    val json: JsonFile = getRegisteredJson(fileName)
    json.dateValue(key)
  }

  def dateOption(fileName: String, key: String): Option[Date] = {
    val json: JsonFile = getRegisteredJson(fileName)
    json.dateOption(key)
  }

  def doubleValue(fileName: String, key: String): Double = {
    val json: JsonFile = getRegisteredJson(fileName)
    json.doubleValue(key)
  }

  def doubleOption(fileName: String, key: String): Option[Double] = {
    val json: JsonFile = getRegisteredJson(fileName)
    json.doubleOption(key)
  }

  def write(fileName: String): Unit = {
    val json: JsonFile = getRegisteredJson(fileName)
    json.write
  }

  private def getRegisteredJson(fileName: String) = {
    internalMap.getOrElse(fileName, throw new FileNotFoundException(s"$fileName not registered"))
  }

}