package com.wa9nnn.multicasttool.multicast

import com.codahale.metrics.Meter
import com.wa9nnn.util.DurationFormat
import scalafx.beans.property.{ObjectProperty, ReadOnlyStringWrapper, StringProperty}
import scalafx.scene.control.TableCell

import java.time.{Duration, Instant}

/**
 * for one node
 *
 * @param intialMessage
 */
case class NodeStats(intialMessage: UdpMessage) {
  private var lastStamp = Instant.EPOCH
  val host: StringProperty = StringProperty(intialMessage.protocol + ":" + intialMessage.host)
  val totalReceived: ObjectProperty[Long] = new ObjectProperty[Long](this, "TotalVCount", 0)
  val lastSn: ObjectProperty[Long] = new ObjectProperty[Long](this, "S/N", 0)
  private var missed = 0
  private val meter = new Meter()
  var lastMessage: StringProperty = StringProperty("?")
  var since: StringProperty = StringProperty("?")
  var sinceStyle = ""

  def tick(): Unit = {
    val duration = Duration.between(lastStamp, Instant.now())
    since.value  = DurationFormat(duration)
    sinceStyle = if (duration.toSeconds > 4)
      "tooOld"
    else
      "isNew"
  }

  def add(message: UdpMessage): Unit = {
    lastStamp = message.stamp
    lastMessage.value = message.toString
    lastSn() = message.sn
    totalReceived() = totalReceived.value + 1L
  }
}

