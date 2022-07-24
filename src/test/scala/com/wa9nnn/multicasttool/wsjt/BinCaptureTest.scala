package com.wa9nnn.multicasttool.wsjt

class BinCaptureTest extends org.specs2.mutable.Specification {
  "roundtrip" >> {
    val binCapture = new BinCapture("bincaptureTest.bin", true)

    val m0 = MessageDebug(MessageType.STATUS, Array(1.byteValue(), 2.byteValue()))
    val m1 = MessageDebug(MessageType.STATUS, Array(10.byteValue(), 12.byteValue()))
    val expected =
      Seq(
        m0,
        m1
      )
    binCapture.write(expected.head)
    binCapture.write(expected(1))
    val read: Seq[MessageDebug] = binCapture.read.toSeq

    read must beEqualTo(expected)
  }
}
