package com.wa9nnn.multicasttool.scalafx.wsjt

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.multicasttool.wsjt.messages.Message
import scalafx.collections.{ObservableArray, ObservableBuffer}

class MessageCircularBuffer(capacity: Int) extends ObservableBuffer[Message]() with LazyLogging {

  def add(message: Message): Unit = {
    prepend(message)

    val deff = length - capacity
    if (deff > 0)
      dropRightInPlace(deff)
  }
}
