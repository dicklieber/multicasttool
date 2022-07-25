package com.wa9nnn.multicasttool.wsjt.messages

import com.wa9nnn.multicasttool.wsjt.Parser.{bool, qint8, quint32, quint64, utf8}
import com.wa9nnn.multicasttool.wsjt.{DecodeException, MessageDebug, MessageType, Parser}
import com.wa9nnn.multicasttool.wsjt.messages.Message._


case class StatusMessage(
                          id: Utf8,
                          dialFrequency: Quint64,
                          mode: Utf8,
                          dxCall: Utf8,
                          report: Utf8,
                          txMode: Utf8,
                          txEnabled: QBool,
                          transmitting: QBool,
                          decoding: QBool,
                          rxDf: Quint32,
                          txDf: Quint32,
                          deCall: Utf8,
                          deGrid: Utf8,
                          dxGrid: Utf8,
                          txWatachDog: QBool,
                          subMode: Utf8,
                          fastMode: QBool,
                          specialOpMode: Quint8,
                          frequencyTolerence: Quint32,
                          trPeriod: Quint32,
                          configName: Utf8,
                          txMessage: Utf8,
                          debug: Option[MessageDebug] = None) extends Message

object StatusMessage {
  def apply()(implicit parser: Parser, mt: MessageType, bin: Option[String]): StatusMessage = {
    try {
      val message = new StatusMessage(
        id = utf8(),
        dialFrequency = quint64(),
        mode = utf8(),
        dxCall = utf8(),
        report = utf8(),
        txMode = utf8(),
        txEnabled = bool(),
        transmitting = bool(),
        decoding = bool(),
        rxDf = quint32(),
        txDf = quint32(),
        deCall = utf8(),
        deGrid = utf8(),
        dxGrid = utf8(),
        txWatachDog = bool(),
        subMode = utf8(),
        fastMode = bool(),
        specialOpMode = qint8(),
        frequencyTolerence = quint32(),
        trPeriod = quint32(),
        configName = utf8(),
        txMessage = utf8(),
        debug = bin.map(MessageDebug(mt, _))
      )
      message
    } catch {
      case e:Exception =>
        throw DecodeException(mt, e)
    }
  }
}

