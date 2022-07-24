package com.wa9nnn.multicasttool.wsjt

import play.api.libs.json.Json

import java.nio.file.StandardOpenOption._
import java.nio.file.{Files, Path, Paths}
import scala.io.BufferedSource

class BinCapture(fileName: String = "wsjtCapture.txt", deleteOnSart: Boolean = false) {
  private val path: Path = Paths.get(fileName)

  if (deleteOnSart)
    Files.delete(path)

  def write(messageDebug: Option[MessageDebug]): Unit = {
    messageDebug.foreach(write)
  }

  def write(messageDebug: MessageDebug): Unit = {
    val line: String = Json.toJson(messageDebug).toString() + System.lineSeparator()
    Files.writeString(path, line, WRITE, APPEND, CREATE)

  }

  def read: IterableOnce[MessageDebug] = {
    val fileNameSource: BufferedSource = io.Source.fromFile(fileName)
    fileNameSource.getLines().map { line =>
      Json.parse(line).as[MessageDebug]
    }
  }
}
