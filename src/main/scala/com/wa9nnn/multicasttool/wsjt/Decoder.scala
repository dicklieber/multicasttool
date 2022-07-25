package com.wa9nnn.multicasttool.wsjt

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.multicasttool.wsjt.MessageType._
import com.wa9nnn.multicasttool.wsjt.Parser._
import com.wa9nnn.multicasttool.wsjt.messages.{ClearMessage, DecodeMessage, HeartbeatMessage, LoggedMessage, Message, StatusMessage}
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
      if (receivedSchemaVersion != 2)
        throw new IllegalArgumentException(s"Expecting schema of ${3} but got $receivedSignature")

      val types: Array[MessageType] = MessageType.values()
      implicit val messageType: MessageType = types(quint32)

      implicit val base64bin = Option.when(logger.underlying.isDebugEnabled()) {
        Base64.encodeBase64String(in)
      }

      val r = messageType match {
        case HEARTBEAT => HeartbeatMessage()
        case STATUS => StatusMessage()
        case DECODE => DecodeMessage()
        case CLEAR => ClearMessage()
        case REPLY => throw NoHandlerException() //todo
        case QSO_LOGGED => LoggedMessage()
        case CLOSE => throw NoHandlerException() //todo
        case REPLAY => throw NoHandlerException() //todo
        case HALT_TX => throw NoHandlerException() //todo
        case FREE_TEXT => throw NoHandlerException() //todo
        case WSPR_DECODE => throw NoHandlerException() //todo
      }
      binCapture.write(r.debug)
      r
    }
  }


}

case class NoHandlerException()(implicit messageType: MessageType) extends Exception(s"No Handler for: $messageType")

case class DecodeException(messageType: MessageType, cause: Throwable) extends Throwable