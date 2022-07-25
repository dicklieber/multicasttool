package com.wa9nnn.multicasttool.wsjt

import com.wa9nnn.multicasttool.wsjt.Message._
import com.wa9nnn.multicasttool.wsjt.Parser._

import java.time.{Instant, LocalTime}

trait Message {
  val id: String
  val debug: Option[MessageDebug]
}


object Message {
  type Utf8 = String
  type QBool = Boolean
  type Quint32 = Int
  type Quint64 = Long
  type QDateTime = Instant
  type QTime = LocalTime
  type QFloat = Double

}


case class DecodeMessage(id: Utf8,
                         newDecode: QBool,
                         time: QTime,
                         snr: Quint32,
                         deltaTime: QFloat,
                         deltaFrequency: Quint64,
                         mode: Utf8,
                         message: Utf8,
                         lowConfidence: QBool,
                         debug: Option[MessageDebug] = None) extends Message

object DecodeMessage {
  def apply()(implicit parser: Parser, mt: MessageType, bin: Option[String]): DecodeMessage = {
    val message = new DecodeMessage(
      id = utf8,
      newDecode = bool(),
      time = qtime(),
      snr = quint32(),
      deltaTime = float(),
      deltaFrequency = quint32,
      mode = utf8(),
      message = utf8(),
      lowConfidence = bool(),
      debug = bin.map(MessageDebug(mt, _))
    )
    message
  }


  case class LoggedMessage(id: Utf8,
                           timeOff: QDateTime,
                           dxCall: Utf8,
                           dxGrid: Utf8,
                           tzFreqHz: Quint64,
                           mode: Utf8,
                           reportSent: Utf8,
                           reportReceived: Utf8,
                           txPower: Utf8,
                           comments: Utf8,
                           name: Utf8,
                           timeOn: QDateTime,
                           operatorCall: Utf8,
                           myCall: Utf8,
                           myGrid: Utf8,
                           exchangeSent: Utf8,
                           exchangeReceived: Utf8,
                           adifPropagationMode: Utf8,
                           debug: Option[MessageDebug] = None
                          ) extends Message

  object LoggedMessage {
    def apply()(implicit parser: Parser, mt: MessageType, bin: Option[String]): LoggedMessage = {
      try {
        val r = new LoggedMessage(
          id = utf8,
          timeOff = qDateTime(),
          dxCall = utf8(),
          dxGrid = utf8(),
          tzFreqHz = quint64(),
          mode = utf8(),
          reportSent = utf8(),
          reportReceived = utf8(),
          txPower = utf8(),
          comments = utf8(),
          name = utf8(),
          timeOn = qDateTime(),
          operatorCall = utf8(),
          myCall = utf8(), myGrid = utf8(),
          exchangeSent = utf8(),
          exchangeReceived = utf8(),
          adifPropagationMode = utf8(),
          debug = bin.map(MessageDebug(mt, _))
        )
        r
      } catch {
        case e: Exception =>
          throw DecodeException(mt, e)
      }
    }
  }


  case class HeartbeatMessage(
                               id: Utf8,
                               maxSchemaNumber: Quint32,
                               version: Utf8,
                               revision: Utf8,
                               debug: Option[MessageDebug] = None

                             ) extends Message

  object HeartbeatMessage {
    def apply()(implicit parser: Parser, mt: MessageType, bin: Option[String]): HeartbeatMessage = {
      val message = new HeartbeatMessage(
        id = utf8(),
        maxSchemaNumber = quint32(),
        version = utf8(),
        revision = utf8(),
        debug = bin.map(MessageDebug(mt, _))
      )
      message
    }
  }
}