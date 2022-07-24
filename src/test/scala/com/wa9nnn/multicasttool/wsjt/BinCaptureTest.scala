package com.wa9nnn.multicasttool.wsjt

class BinCaptureTest extends org.specs2.mutable.Specification {
  "roundtrip" >> {
    val binCapture = new BinCapture("bincaptureTest.bin", true)

    val bytes0: Array[Byte] = Array(1.byteValue(), 2.byteValue())
    val bytes1: Array[Byte] = Array(10.byteValue(), 12.byteValue())
    val expected: Seq[Array[Byte]] =
      Seq(
        bytes0,
        bytes1
      )
    binCapture.write(expected.head)
    binCapture.write(expected(1))


    val read: IterableOnce[Array[Byte]] = binCapture.read
    read.iterator.zipWithIndex.foreach { case (bytes, i) =>
      println(s"$i: ${bytes.map { byte => byte.toHexString }.mkString(",")}")
      if (!(expected(i) sameElements bytes)) {
        println(s"Error:: $i: ${bytes.map { byte => byte.toHexString }.mkString(",")}")
        throw new Exception(s"i: $i mismatch!")
      }
    }
    ok
  }
}
