package com.wa9nnn.multicasttool.wsjt

import org.apache.commons.codec.binary.{Base64, Base64OutputStream}

import java.io.OutputStream
import java.nio.file.StandardOpenOption._
import java.nio.file.{Files, Path, Paths}
import scala.io.BufferedSource

class BinCapture(fileName: String = "wsjtCapture.txt", deleteOnSart:Boolean = false) {
  private val path: Path = Paths.get(fileName)

  if(deleteOnSart)
    Files.delete(path)

  def write(bytes: Array[Byte]): Unit = {
    val outputStream: OutputStream = Files.newOutputStream(path, WRITE, APPEND, CREATE)
    val base64OutputStream = new Base64OutputStream(outputStream)
    base64OutputStream.write(bytes)
    base64OutputStream.write('\n')
    base64OutputStream.close()
  }

  def read: IterableOnce[Array[Byte]] = {
    val fileNameSource: BufferedSource = io.Source.fromFile(fileName)
    fileNameSource.getLines().map { line =>
      val bytes = Base64.decodeBase64(line.dropRight(1))
      bytes
    }
  }
}
