package com.wa9nnn.multicasttool

import com.codahale.metrics.Meter
import scalafx.beans.property.{LongProperty, ObjectProperty, StringProperty}

import java.util.concurrent.atomic.AtomicInteger

case class NodeStats(intialMessage: Message) {
  val host: StringProperty = StringProperty(intialMessage.host)
  val totalReceived: ObjectProperty[Long] = new ObjectProperty[Long](this, "TotalVCount", 0)
  val lastSn: ObjectProperty[Long] = new ObjectProperty[Long](this, "S/N", 0)
  private var missed = 0
  private val meter = new Meter()
  var lastMessage: StringProperty = StringProperty("?")

  def add(message: Message): Unit = {
    lastMessage.value = message.toString
    lastSn() = message.sn
    totalReceived() = totalReceived.value + 1L
  }
}