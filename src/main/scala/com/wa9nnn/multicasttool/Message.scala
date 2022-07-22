package com.wa9nnn.multicasttool

import play.api.libs.json.{Json, OFormat}

import java.time.Instant

case class Message(host:String, sn:Long, stamp:Instant = Instant.now())

object Message {
  implicit val fmt: OFormat[Message] = Json.format[Message]
}
