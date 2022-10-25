package dev.quadstingray.sbt.json

import better.files.File
import dev.quadstingray.sbt.json.JsonFile.{ convertJsonToString, convertToMutableMap }
import io.circe.parser.*
import io.circe.syntax.*
import org.joda.time.DateTime

import java.util.Date
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

case class JsonFile(file: File, jsonMap: mutable.LinkedHashMap[String, Any]) {

  private def findMapWithKey(key: String, map: mutable.LinkedHashMap[String, Any]): Option[(String, mutable.LinkedHashMap[String, Any])] = {
    if (map.contains(key)) {
      Some((key, map))
    }
    else {
      val parentKeys = ArrayBuffer[String]()
      var currentKey = ""
      key
        .split('.')
        .foreach(part => {
          if (currentKey != "") {
            currentKey = currentKey + "."
          }
          currentKey = currentKey + part
          parentKeys += currentKey
        })
      parentKeys
        .flatMap(internalKey => {
          if (map.contains(internalKey)) {
            val internalMap = map(internalKey).asInstanceOf[mutable.LinkedHashMap[String, Any]]
            findMapWithKey(key.replace(s"$internalKey.", ""), internalMap)
          }
          else {
            None
          }
        })
        .headOption
    }
  }

  def updateValue(key: String, value: Any): Unit = {
    val keyMapElement = findMapWithKey(key, jsonMap).getOrElse(throw new java.util.NoSuchElementException(s"key not found: $key in $file"))
    if (value.isInstanceOf[Map[String, Any]]) {
      keyMapElement._2.put(keyMapElement._1, convertToMutableMap(value.asInstanceOf[Map[String, Any]]))
    }
    else {
      keyMapElement._2.put(keyMapElement._1, value)
    }
  }

  def write(): Unit = {
    file.delete()
    file.createFile()
    val prettyJsonString = convertJsonToString(this)
    file.append(prettyJsonString)
  }

  def value(key: String): Any = {
    val keyMapElement = findMapWithKey(key, jsonMap).getOrElse(throw new java.util.NoSuchElementException(s"key not found: $key in $file"))
    keyMapElement._2(keyMapElement._1)
  }

  def valueOption(key: String): Option[Any] = {
    val keyMapElement = findMapWithKey(key, jsonMap).getOrElse(throw new java.util.NoSuchElementException(s"key not found: $key in $file"))
    keyMapElement._2.get(keyMapElement._1)
  }

  def stringValue(key: String): String = {
    value(key).toString
  }

  def stringOption(key: String): Option[String] = {
    valueOption(key).map(_.toString)
  }

  def stringListValue(key: String): List[String] = {
    val internalValue = value(key)
    internalValue match {
      case list: List[Any] =>
        list.map(_.toString)
      case _ =>
        throw new Exception(s"Value for Key $key is not an value of type List")
    }
  }

  def longValue(key: String): Long = {
    val internalValue = value(key)
    internalValue match {
      case d: Double =>
        d.toLong
      case i: Int =>
        i.toLong
      case l: Long =>
        l
      case bi: BigInt =>
        bi.toLong
      case bd: BigDecimal =>
        bd.toLong
      case _ =>
        throw new Exception(s"Value for Key $key is not an value of type number")
    }
  }

  def longOption(key: String): Option[Long] = {
    valueOption(key).map {
      case d: Double =>
        d.toLong
      case i: Int =>
        i.toLong
      case l: Long =>
        l
      case bi: BigInt =>
        bi.toLong
      case bd: BigDecimal =>
        bd.toLong
      case _ =>
        throw new Exception(s"Value for Key $key is not an value of type number")
    }
  }

  def intValue(key: String): Int = {
    val internalValue = value(key)
    internalValue match {
      case d: Double =>
        d.toInt
      case i: Int =>
        i
      case l: Long =>
        l.toInt
      case bi: BigInt =>
        bi.toInt
      case bd: BigDecimal =>
        bd.toInt
      case _ =>
        throw new Exception(s"Value for Key $key is not an value of type number")
    }
  }

  def intOption(key: String): Option[Int] = {
    valueOption(key).map {
      case d: Double =>
        d.toInt
      case i: Int =>
        i
      case l: Long =>
        l.toInt
      case bi: BigInt =>
        bi.toInt
      case bd: BigDecimal =>
        bd.toInt
      case _ =>
        throw new Exception(s"Value for Key $key is not an value of type number")
    }
  }

  def dateValue(key: String): Date = {
    val internalValue = value(key)
    if (internalValue != null) {
      new DateTime(internalValue.toString).toDate
    }
    else {
      throw new Exception(s"Value for Key $key is not an value of type List")
    }
  }

  def dateOption(key: String): Option[Date] = {
    valueOption(key).map(internalValue => new DateTime(internalValue.toString).toDate)
  }

  def doubleValue(key: String): Double = {
    val internalValue = value(key)
    internalValue match {
      case d: Double =>
        d
      case i: Int =>
        i.toDouble
      case l: Long =>
        l.toDouble
      case bi: BigInt =>
        bi.toDouble
      case bd: BigDecimal =>
        bd.toDouble
      case _ =>
        throw new Exception(s"Value for Key $key is not an value of type number")
    }
  }

  def doubleOption(key: String): Option[Double] = {
    valueOption(key).map {
      case d: Double =>
        d
      case i: Int =>
        i.toDouble
      case l: Long =>
        l.toDouble
      case bi: BigInt =>
        bi.toDouble
      case bd: BigDecimal =>
        bd.toDouble
      case _ =>
        throw new Exception(s"Value for Key $key is not an value of type number")
    }
  }

}

object JsonFile extends CirceParsingHelper {

  private def checkAndValidate(map: mutable.LinkedHashMap[String, Any]): mutable.LinkedHashMap[String, Any] = {
    map.foreach(element => {
      if (element._2.isInstanceOf[Map[String, Any]]) {
        map.put(element._1, convertToMutableMap(element._2.asInstanceOf[Map[String, Any]]))
      }
    })
    map
  }

  private def convertToMutableMap(map: Map[String, Any]): mutable.LinkedHashMap[String, Any] = {
    val mutableMap = mutable.LinkedHashMap[String, Any]()
    map.foreach(element => {
      if (element._2.isInstanceOf[Map[String, Any]]) {
        mutableMap.put(element._1, convertToMutableMap(element._2.asInstanceOf[Map[String, Any]]))
      }
      else {
        mutableMap.put(element._1, element._2)
      }
    })
    mutableMap
  }

  private def convertJsonToString(jsonFile: JsonFile): String = {
    val prettyJsonString = jsonFile.jsonMap.asJson.toString()
    prettyJsonString
  }
  def apply(file: sbt.File): JsonFile = {
    val bFile      = File(file.toURI)
    val jsonString = bFile.contentAsString
    val jsonMap    = decode[mutable.LinkedHashMap[String, Any]](jsonString)
    JsonFile(bFile, checkAndValidate(jsonMap.getOrElse(throw new Exception(s"Could not parse file ${bFile.toString()}"))))
  }

}
