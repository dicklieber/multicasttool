package com.wa9nnn.multicasttool.wsjt.messages

import com.wa9nnn.multicasttool.wsjt.Parser.{bool, float, qtime, quint32, utf8}
import com.wa9nnn.multicasttool.wsjt.messages.Message._
import com.wa9nnn.multicasttool.wsjt.{MessageDebug, MessageType, Parser}

/**
 * Sent when WSJT-X decodes a message
 * @param id
 * @param newDecode
 * @param time
 * @param snr
 * @param deltaTime
 * @param deltaFrequency
 * @param mode
 * @param message
 * @param lowConfidence
 * @param debug
 */
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
}
