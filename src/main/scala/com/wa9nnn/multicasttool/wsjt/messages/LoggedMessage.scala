package com.wa9nnn.multicasttool.wsjt.messages

import com.wa9nnn.multicasttool.wsjt.{DecodeException, MessageDebug, MessageType, Parser}
import com.wa9nnn.multicasttool.wsjt.Parser.{qDateTime, quint64, utf8}
import com.wa9nnn.multicasttool.wsjt.messages.Message.{QDateTime, Quint64, Utf8}


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
