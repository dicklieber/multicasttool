package com.wa9nnn.multicasttool.scalafx.wsjt

import com.wa9nnn.multicasttool.wsjt.{MessageDebug, MessageType}
import com.wa9nnn.multicasttool.wsjt.messages.{Message, TestMessage}
import org.specs2.mutable.Specification

class MessageCircularBufferSpec extends Specification {

  "BufferSpec" should {
    "empty" in {
      val buffer = new MessageCircularBuffer(10)
      buffer.size must beEqualTo(0)
    }


    "add 1" in {
      val buffer = new MessageCircularBuffer(10)
      val testMessage = TestMessage("1")
      buffer.add(testMessage)
      buffer.size must beEqualTo(1)
      buffer.head must beEqualTo (testMessage)
    }

    "capacity " should {
      val capacity = 5
      "handle at capacity" in {
        val buffer = new MessageCircularBuffer(capacity)
        for {
          i <- 1 to 10
        } {
          buffer.add(TestMessage(i.toString))
        }
        buffer.size must beEqualTo (capacity)
      }
      "exceed by one" in {
        val buffer = new MessageCircularBuffer(capacity)
        for {
          i <- 1 to 11
        } {
          buffer.add(TestMessage(i.toString))
        }
        buffer.size must beEqualTo (capacity)
      }
      "exceed by many" in {
        val buffer = new MessageCircularBuffer(capacity)
        for {
          i <- 1 to 100
        } {
          buffer.add(TestMessage(i.toString))
        }
        buffer.size must beEqualTo (capacity)
      }

    }

  }
}


