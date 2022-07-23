package com.wa9nnn.multicasttool.wsjt

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.multicasttool.wsjt.MessageType.{CLEAR, CLOSE, DECODE, FREE_TEXT, HALT_TX, HEARTBEAT, QSO_LOGGED, REPLAY, REPLY, STATUS, WSPR_DECODE, values}
import org.apache.commons.io.HexDump

import java.nio.ByteBuffer
import java.time.LocalTime
import java.util.concurrent.TimeUnit.MILLISECONDS
import scala.util.Random.nextInt
import scala.util.Try

object Parser extends LazyLogging {
  val signature: Int = 0xADBCCBDA

  def apply(in: Array[Byte]): Try[Message] = {
    val buffer = ByteBuffer.wrap(in)

    def nextInt(): Int = buffer.getInt

    def nextString(): String = {
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

    def nextBool(): Boolean = {
      buffer.get() != 0
    }


    def nextTime() = {
      val ns = MILLISECONDS.convert(nextInt(), MILLISECONDS)
      LocalTime.ofNanoOfDay(ns)
    }
    def decodeMessage(): DecodeMessage = {
      new DecodeMessage(
        id = nextString(),
        newDecode = nextBool(),
        time = nextTime(),
        snr = buffer.getInt(),
        deltaTime = buffer.getDouble(),
        deltaFrequency = buffer.getInt(),
        mode = nextString(),
        message = nextString(),
        lowConfidence = nextBool(),
      )


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

     val r =  values()(buffer.getInt) match {
        case HEARTBEAT => throw new NotImplementedError() //todo
        case STATUS => throw new NotImplementedError() //todo
        case DECODE => decodeMessage()
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

case class DecodeMessage(id: String, newDecode: Boolean, time: LocalTime, snr: Long,
                         deltaTime: Double, deltaFrequency: Int,
                         mode: String, message: String, lowConfidence: Boolean) extends Message

