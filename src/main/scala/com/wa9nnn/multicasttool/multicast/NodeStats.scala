package com.wa9nnn.multicasttool.multicast

import com.codahale.metrics.Meter
import com.wa9nnn.util.DurationFormat
import scalafx.beans.property.{ObjectProperty, ReadOnlyStringWrapper, StringProperty}

/**
 * for one node
 *
 * @param intialMessage
 */
case class NodeStats(intialMessage: UdpMessage) {
  private var currentMessage = intialMessage
  val host: StringProperty = StringProperty(intialMessage.host)
  val totalReceived: ObjectProperty[Long] = new ObjectProperty[Long](this, "TotalVCount", 0)
  val lastSn: ObjectProperty[Long] = new ObjectProperty[Long](this, "S/N", 0)
  private var missed = 0
  private val meter = new Meter()
  var lastMessage: StringProperty = StringProperty("?")
  val since: StringProperty = StringProperty("?")

  def tick(): Unit = {
    since.value = DurationFormat(currentMessage.stamp)
  }



  //  def age:Duration = Duration.between(last, Instant.now())

  def add(message: UdpMessage): Unit = {
    currentMessage = message
    lastMessage.value = message.toString
    lastSn() = message.sn
    totalReceived() = totalReceived.value + 1L
  }
}
