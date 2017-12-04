package com.duowan.meteor.server.util

import java.io.File
import java.util.Properties
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.exception.ExceptionUtils
import java.io.FileInputStream

object PropertiesUtil extends Logging {

  val p: Properties = new Properties()

  def load(file: String): Unit = synchronized {
    if (p.size() > 0) return
    var fis: FileInputStream = null
    try {
      fis = FileUtils.openInputStream(new File(file))
      p.load(fis)
    } catch {
      case e: Exception => logError(ExceptionUtils.getFullStackTrace(e))
    } finally {
      IOUtils.closeQuietly(fis)
    }
  }

  def get(key: String): String = {
    p.getProperty(key)
  }
  
  def get(key: String, defaultValue: String): String = {
    p.getProperty(key, defaultValue)
  }
}