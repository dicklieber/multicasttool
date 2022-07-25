package com.wa9nnn.multicasttool.wsjt.messages

import com.wa9nnn.multicasttool.wsjt.Parser._
import com.wa9nnn.multicasttool.wsjt.messages.Message._
import com.wa9nnn.multicasttool.wsjt.{DecodeException, MessageDebug, MessageType, Parser}

import java.time.{Instant, LocalTime}

trait Message {
  val id: String
  val debug: Option[MessageDebug]
}

/**
 * Define types to match QT message types
 */
object Message {
  type Utf8 = String
  type QBool = Boolean
  // Java/Scala doesn't have unsinged types. Hoping the signed version will be big enough.
  type Quint32 = Int
  type Quint64 = Long
  type QDateTime = Instant
  type QTime = LocalTime
  type QFloat = Double
}


