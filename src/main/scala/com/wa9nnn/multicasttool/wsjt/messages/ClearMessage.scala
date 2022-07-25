package com.wa9nnn.multicasttool.wsjt.messages

import com.wa9nnn.multicasttool.wsjt.Parser.utf8
import com.wa9nnn.multicasttool.wsjt.messages.Message.{Quint8, Utf8}
import com.wa9nnn.multicasttool.wsjt.{MessageDebug, MessageType, Parser}


case class ClearMessage(
                             id: Utf8,
                             window:Quint8 = 0.asInstanceOf[Byte], // in only
                             debug: Option[MessageDebug] = None
                           ) extends Message

object ClearMessage {
  def apply()(implicit parser: Parser, mt: MessageType, bin: Option[String]): ClearMessage = {
    val message = new ClearMessage(
      id = utf8(),
      debug = bin.map(MessageDebug(mt, _))
    )
    message
  }
}