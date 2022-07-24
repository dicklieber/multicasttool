package com.wa9nnn.multicasttool.wsjt

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.multicasttool.wsjt.MessageType.{CLEAR, CLOSE, DECODE, FREE_TEXT, HALT_TX, HEARTBEAT, QSO_LOGGED, REPLAY, REPLY, STATUS, WSPR_DECODE, values}
import org.apache.commons.io.HexDump

import java.nio.ByteBuffer
import java.time.{Instant, LocalTime}
import java.util.concurrent.TimeUnit.MILLISECONDS
import scala.language.implicitConversions
import scala.util.Random.nextInt
import scala.util.Try

object ParserThing extends LazyLogging {
  val signature: Int = 0xADBCCBDA

  def apply(in: Array[Byte]): Try[Message] = {
    val buffer = ByteBuffer.wrap(in)

    implicit def nextInt(buffer: ByteBuffer): Int = buffer.getInt

    implicit def nextLong(buffer: ByteBuffer): Long = buffer.getLong

    implicit def nextString(buffer: ByteBuffer): String = {
      //      if (byteCount <= nextByte) {
      //        log.debug("nextString when byteCount is {} and nextByte is {}", byteCount, nextByte)
      //        return ""
      //      }
      val strSize: Byte = buffer.get()
      if (strSize == 0xffffffff) return "" // Return an empty rather than null
      val sBytes = new Array[Byte](strSize)
      buffer.get(sBytes)

      new String(sBytes)
    }

    implicit def nextBool(buffer: ByteBuffer): Boolean = {
      buffer.get() != 0
    }

    implicit def nextDouyble(buffer: ByteBuffer): Double = {
      buffer.getDouble
    }


    def nextTime() = {
      val ns = MILLISECONDS.convert(nextInt(buffer), MILLISECONDS)
      LocalTime.ofNanoOfDay(ns)
    }


    Try {

      HexDump.dump(in, 0, System.out, 0)

      val javaParser = new JavaParser(in, in.length)
      val message = javaParser.decode()


      val receivedSignature = buffer.getInt
      if (receivedSignature != signature)
        throw new IllegalArgumentException(s"Expecting signature of ${signature.toHexString} but got ${receivedSignature.toHexString}")

      val receivedSchemaVersion = buffer.getInt
      if (receivedSchemaVersion != 3)
        throw new IllegalArgumentException(s"Expecting schema of ${3} but got $receivedSignature")

      val r = values()(buffer.getInt) match {
        case HEARTBEAT => throw new NotImplementedError() //todo
        case STATUS => throw new NotImplementedError() //todo
        case DECODE => throw new NotImplementedError() //todo
        //        case DECODE => decodeMessage()
        case CLEAR => throw new NotImplementedError() //todo
        case REPLY => throw new NotImplementedError() //todo
        case QSO_LOGGED => throw new NotImplementedError() //todo
        case CLOSE => throw new NotImplementedError() //todo
        case REPLAY => throw new NotImplementedError() //todo
        case HALT_TX => throw new NotImplementedError() //todo
        case FREE_TEXT => throw new NotImplementedError() //todo
        case WSPR_DECODE => throw new NotImplementedError() //todo
      }
      logger.debug(message.toString)
      r

    }


  }
}


