package com.wa9nnn.sbttest

import com.codahale.metrics.Meter
import scalafx.beans.property.StringProperty

import java.util.concurrent.atomic.AtomicInteger

case class NodeStats(intialMessage: Message) {
  val host: StringProperty = StringProperty(intialMessage.host)
  val totalReceived: AtomicInteger =  new AtomicInteger()
  private var lastSn = 0
  private var missed = 0
  private val meter = new Meter()
  var lastMessage: StringProperty = StringProperty("?")

  def add(message: Message): Unit = {
    val missed = message.sn - lastSn
    lastMessage.value = message.toString
    totalReceived.incrementAndGet()
  }

}
