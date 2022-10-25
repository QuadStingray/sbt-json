package dev.quadstingray.sbt.json

import com.typesafe.scalalogging.LazyLogging

object JsonLogger extends LazyLogging {
  var sbtLogger: sbt.Logger = _

  def error(message: => String): Unit = {
    if (sbtLogger != null) {
      sbtLogger.error(message)
    }
    else {
      logger.error(message)
    }
  }

  def warn(message: => String): Unit = {
    if (sbtLogger != null) {
      sbtLogger.warn(message)
    }
    else {
      logger.warn(message)
    }
  }

  def debug(message: => String): Unit = {
    if (sbtLogger != null) {
      sbtLogger.debug(message)
    }
    else {
      logger.debug(message)
    }
  }

  def info(message: => String): Unit = {
    if (sbtLogger != null) {
      sbtLogger.info(message)
    }
    else {
      logger.info(message)
    }
  }
}
