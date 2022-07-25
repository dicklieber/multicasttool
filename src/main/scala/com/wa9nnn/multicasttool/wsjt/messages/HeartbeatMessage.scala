package com.wa9nnn.multicasttool.wsjt.messages

import com.wa9nnn.multicasttool.wsjt.{MessageDebug, MessageType, Parser}
import com.wa9nnn.multicasttool.wsjt.Parser.{quint32, utf8}
import com.wa9nnn.multicasttool.wsjt.messages.Message.{Quint32, Utf8}


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