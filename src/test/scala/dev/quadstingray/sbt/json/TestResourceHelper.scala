package dev.quadstingray.sbt.json

import java.io.File
import java.nio.file.{Files, StandardCopyOption}

trait TestResourceHelper {
  def resourceFile(resourcePath: String): File = {
    val url = getClass.getResource(resourcePath)
    require(url != null, s"Resource not found: $resourcePath")
    if (url.getProtocol == "file") {
      new File(url.toURI)
    } else {
      val name    = resourcePath.substring(resourcePath.lastIndexOf('/') + 1)
      val tempDir = Files.createTempDirectory("sbt-json-test-")
      tempDir.toFile.deleteOnExit()
      val temp = new File(tempDir.toFile, name)
      temp.deleteOnExit()
      val in = url.openStream()
      try Files.copy(in, temp.toPath, StandardCopyOption.REPLACE_EXISTING)
      finally in.close()
      temp
    }
  }
}
