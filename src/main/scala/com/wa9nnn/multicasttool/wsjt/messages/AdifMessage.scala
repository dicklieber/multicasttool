package com.wa9nnn.multicasttool.wsjt.messages

import com.wa9nnn.multicasttool.wsjt.Parser._
import com.wa9nnn.multicasttool.wsjt.{MessageDebug, MessageType, Parser}
import com.wa9nnn.multicasttool.wsjt.messages.Message._

case class AdifMessage(
                        id: Utf8,
                        adif: Utf8,
                        debug: Option[MessageDebug] = None

                      ) extends Message

object AdifMessage {
  def apply()(implicit parser: Parser, mt: MessageType, bin: Option[String]): AdifMessage = {
    val message = new AdifMessage(
      id = utf8(),
      adif = utf8(),
      debug = bin.map(MessageDebug(mt, _))
    )
    message
  }
}