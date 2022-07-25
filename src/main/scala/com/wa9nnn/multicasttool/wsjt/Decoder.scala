package com.wa9nnn.multicasttool.wsjt

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.multicasttool.wsjt.MessageType._
import com.wa9nnn.multicasttool.wsjt.Parser._
import com.wa9nnn.multicasttool.wsjt.messages.{AdifMessage, ClearMessage, DecodeMessage, HeartbeatMessage, LoggedMessage, Message, StatusMessage}
import org.apache.commons.codec.binary.Base64

import scala.util.Try
object Decoder extends LazyLogging {
  val signature: Long = 0xADBCCBDA
  private val binCapture = new BinCapture()

  def apply(in: Array[Byte]): Try[Message] = {
    Try {
      implicit val parser = new Parser(in)
      val receivedSignature = quint32
      if (receivedSignature != signature)
        throw new IllegalArgumentException(s"Expecting signature of ${signature.toHexString} but got ${receivedSignature.toHexString}")

      val receivedSchemaVersion = quint32
      if (receivedSchemaVersion > 3)
        throw new IllegalArgumentException(s"Expecting schema of ${receivedSchemaVersion} but got $receivedSchemaVersion")

      val types: Array[MessageType] = MessageType.values()
      implicit val messageType: MessageType = {
        val nMt = quint32
        try {
          types(nMt)
        } catch {
          case e:ArrayIndexOutOfBoundsException =>
            logger.error("Unknown message type: {}",nMt )
            throw new IllegalArgumentException(e)
        }
      }

      implicit val base64bin = Option.when(logger.underlying.isDebugEnabled()) {
        Base64.encodeBase64String(in)
      }

      val r: Message = messageType match {
        case HEARTBEAT => HeartbeatMessage()
        case STATUS => StatusMessage()
        case DECODE => DecodeMessage()
        case CLEAR => ClearMessage()
        case QSO_LOGGED => LoggedMessage()
        case ADIF => AdifMessage()
        case x =>
          throw NoHandlerException()
      }
      binCapture.write(r.debug)
      r
    }
  }


}

case class NoHandlerException()(implicit messageType: MessageType) extends Exception(s"No Handler for: $messageType")

case class DecodeException(messageType: MessageType, cause: Throwable) extends Throwable