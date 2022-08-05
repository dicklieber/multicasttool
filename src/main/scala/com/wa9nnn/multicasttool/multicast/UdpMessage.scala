package com.wa9nnn.multicasttool.multicast

import play.api.libs.json.{Json, OFormat}

import java.net.InetAddress
import java.time.Instant

case class UdpMessage(host:String, sn:Long, stamp:Instant = Instant.now(), source:Option[String] = None)

object UdpMessage {
  implicit val fmt: OFormat[UdpMessage] = Json.format[UdpMessage]
}
