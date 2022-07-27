package com.wa9nnn.multicasttool.wsjt.messages

import com.wa9nnn.multicasttool.wsjt.Parser._
import com.wa9nnn.multicasttool.wsjt.messages.Message._
import com.wa9nnn.multicasttool.wsjt.{DecodeException, MessageDebug, MessageType, Parser}

import java.time.{Instant, LocalTime}
import java.util.concurrent.atomic.AtomicLong

/**
 * All (output) WSJT UDP messagwes are defined here.
 */
sealed trait Message {
  val id: String
  val debug: Option[MessageDebug]
  val stamp: Instant = Instant.now()
  val messageType: MessageType
  val sn: Long = Message.nexSn

  def detail: String = throw new NotImplementedError()
}

/**
 * Define types to match QT message types
 */
object Message {
  type Utf8 = String
  type QBool = Boolean
  // Java/Scala doesn't have unsinged types. Hoping the signed version will be big enough.
  type Quint8 = Byte
  type Quint32 = Int
  type Quint64 = Long
  type QDateTime = Instant
  type QTime = LocalTime
  type QFloat = Double

  private val snSource = new AtomicLong()
  def nexSn:Long = snSource.incrementAndGet()

  implicit def dbg(implicit mt: MessageType, bin: Option[String]): Option[MessageDebug] = {
    bin.map(MessageDebug(mt, _))
  }
}

case class HeartbeatMessage(
                             messageType: MessageType,
                             id: Utf8,
                             maxSchemaNumber: Quint32,
                             version: Utf8,
                             revision: Utf8,
                             debug: Option[MessageDebug]

                           ) extends Message {
  override def detail: String = {
    s"schema: $maxSchemaNumber version: $version revision: $revision"
  }
}

object HeartbeatMessage {

  def apply()(implicit parser: Parser, mt: MessageType, bin: Option[String]): HeartbeatMessage = {
    val message = new HeartbeatMessage(
      messageType = mt,
      id = utf8(),
      maxSchemaNumber = quint32(),
      version = utf8(),
      revision = utf8(),
      debug = dbg
    )
    message
  }
}

case class AdifMessage(
                        messageType: MessageType,
                        id: Utf8,
                        adif: Utf8,
                        debug: Option[MessageDebug] = None

                      ) extends Message {
  override def detail: String = {
    adif
  }
}

object AdifMessage {
  def apply()(implicit parser: Parser, mt: MessageType, bin: Option[String]): AdifMessage = {
    val message = new AdifMessage(
      messageType = mt,
      id = utf8(),
      adif = utf8(),
      debug = dbg
    )
    message
  }
}

case class ClearMessage(
                         messageType: MessageType,
                         id: Utf8,
                         window: Quint8 = 0.asInstanceOf[Byte], // in only
                         debug: Option[MessageDebug] = None
                       ) extends Message {
  override def detail: String = "Clear decoded"
}

object ClearMessage {
  def apply()(implicit parser: Parser, mt: MessageType, bin: Option[String]): CloseMessage = {
    val message = new CloseMessage(
      messageType = mt,
      id = utf8(),
      debug = dbg
    )
    message
  }
}


case class CloseMessage(
                         messageType: MessageType,
                         id: Utf8,
                         window: Quint8 = 0.asInstanceOf[Byte], // in only
                         debug: Option[MessageDebug] = None
                       ) extends Message {
  override def detail: String = "Close WSJT-X"
}

object CloseMessage {
  def apply()(implicit parser: Parser, mt: MessageType, bin: Option[String]): CloseMessage = {
    val message = new CloseMessage(
      messageType = mt,
      id = utf8(),
      debug = dbg
    )
    message
  }
}

/**
 * Sent when WSJT-X decodes a message
 */
case class DecodeMessage(
                          messageType: MessageType,
                          id: Utf8,
                          newDecode: QBool,
                          time: QTime,
                          snr: Quint32,
                          deltaTime: QFloat,
                          deltaFrequency: Quint64,
                          mode: Utf8,
                          message: Utf8,
                          lowConfidence: QBool,
                          debug: Option[MessageDebug] = None) extends Message {
  override def detail: String = {
    message
  }
}

object DecodeMessage {
  def apply()(implicit parser: Parser, mt: MessageType, bin: Option[String]): DecodeMessage = {
    val message = new DecodeMessage(
      messageType = mt,
      id = utf8,
      newDecode = bool(),
      time = qtime(),
      snr = quint32(),
      deltaTime = float(),
      deltaFrequency = quint32,
      mode = utf8(),
      message = utf8(),
      lowConfidence = bool(),
      debug = dbg
    )
    message
  }
}


case class LoggedMessage(
                          messageType: MessageType,
                          id: Utf8,
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
                        ) extends Message {
  override def detail: String = {
    s"$myCall with $dxCall at $dxGrid using $mode"
  }
}

object LoggedMessage {
  def apply()(implicit parser: Parser, mt: MessageType, bin: Option[String]): LoggedMessage = {
    try {
      val r = new LoggedMessage(
        messageType = mt,
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
        debug = dbg
      )
      r
    } catch {
      case e: Exception =>
        throw DecodeException(mt, e)
    }
  }
}


case class StatusMessage(
                          messageType: MessageType,
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
                          debug: Option[MessageDebug] = None) extends Message {
  override def detail: String = {
    val mhz: Double = dialFrequency * 1000000.0
    f"$mhz%,.5f Mhz $txMode"
  }

}

object StatusMessage {
  def apply()(implicit parser: Parser, mt: MessageType, bin: Option[String]): StatusMessage = {
    try {
      val message = new StatusMessage(
        messageType = mt,
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
        debug = dbg
      )
      message
    } catch {
      case e: Exception =>
        throw DecodeException(mt, e)
    }
  }
}


case class TestMessage(id: String) extends Message {
  override val messageType: MessageType = MessageType.ADIF

  override def detail: String = "Unit test"

  override val debug: Option[MessageDebug] = None
}

