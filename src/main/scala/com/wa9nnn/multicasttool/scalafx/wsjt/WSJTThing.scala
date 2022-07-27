package com.wa9nnn.multicasttool.scalafx.wsjt

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.multicasttool.wsjt.WSJT
import com.wa9nnn.multicasttool.wsjt.messages.{HeartbeatMessage, Message, StatusMessage}
import com.wa9nnn.util.HostAndPort
import scalafx.beans.property.ObjectProperty

import java.net.InetAddress
import java.util.concurrent.atomic.AtomicInteger

class WSJTThing(hostAndPort: HostAndPort, capacity: Int) extends LazyLogging {
  val messageCircularBuffer = new MessageCircularBuffer(capacity: Int)
  val messageCount = new AtomicInteger()
  val groupAddress: InetAddress = InetAddress.getByName(hostAndPort.host)
  var lastHeartbeat: Option[HeartbeatMessage] = None
  private val status: Option[ObjectProperty[StatusMessage]] = None

  private def handleHeartbeat(heartbeatMessage: HeartbeatMessage): Unit =

    lastHeartbeat = Option(heartbeatMessage)

  def handleStatus(statusMessage: StatusMessage): Unit = {
    status match {
      case Some(value) =>
        logger.debug("status update")
        value.value = statusMessage
      case None =>
        logger.debug("status 1st time")
        ObjectProperty(statusMessage)
    }
  }

  new WSJT(groupAddress, hostAndPort.port, (message:Message) =>{

    message match {
    case hb: HeartbeatMessage => handleHeartbeat(hb)
    case sm: StatusMessage => handleStatus(sm)

    case other =>
    messageCircularBuffer.add(other)

  }
    messageCount.incrementAndGet()

  })



}
