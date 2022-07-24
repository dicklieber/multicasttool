package com.wa9nnn.multicasttool.wsjt

import org.apache.commons.codec.binary.Base64.{decodeBase64, encodeBase64String}
import play.api.libs.json._

import java.time.Instant
import java.util.concurrent.atomic.AtomicInteger
import com.wa9nnn.multicasttool.wsjt.MessageTypeFormat._
/**
 *
 * @param sn          of session
 * @param received    when we got this from WSJT-X
 * @param messageType what WSJT-X is trying to tell us.
 * @param base64      raw binary from UDP message.  As base64 to play nicely within a JSON message.
 */
case class MessageDebug(sn: Int, received: Instant, messageType: MessageType, base64: String) {
  lazy val binary: Array[Byte] = decodeBase64(base64)
}


object MessageDebug {
  implicit val fmrDb: Format[MessageDebug] = Json.format[MessageDebug]


  private val snSource = new AtomicInteger()

  def apply(messageType: MessageType, bin: Array[Byte]): MessageDebug =
    apply(messageType, encodeBase64String(bin))

  def apply(messageType: MessageType, base64: String): MessageDebug = {
    new MessageDebug(snSource.getAndIncrement(), received = Instant.now(), messageType, base64)
  }
}