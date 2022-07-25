package com.wa9nnn.multicasttool.wsjt

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.multicasttool.wsjt.messages.Message.QDateTime
import org.apache.commons.io.HexDump

import java.nio.{ByteBuffer, ByteOrder}
import java.time.{Instant, LocalDate, LocalDateTime, LocalTime, ZoneId, ZoneOffset}
import java.util.Date
import java.util.concurrent.TimeUnit.{MILLISECONDS, NANOSECONDS}
import scala.language.implicitConversions

/**
 * Knows how to parser a QT message.
 *
 * @param in from WSJT UDP server
 */
class Parser(in: Array[Byte]) extends LazyLogging {
  logger.whenDebugEnabled {
    HexDump.dump(in, 0, System.out, 0)
  }

  implicit val buffer: ByteBuffer = ByteBuffer.wrap(in)
  // Ensurfe big endian otherise will use native endian. e.g. little on Intel
  buffer.order(ByteOrder.BIG_ENDIAN)

}

/**
 * Methods to "take" various type field from a WSJT-X UDP binary message.
 * @see https://sourceforge.net/p/wsjt/wsjtx/ci/master/tree/Network/NetworkMessage.hpp
 */
object Parser extends LazyLogging {
  implicit def quint32()(implicit parser: Parser): Int = {
    val int: Int = parser.buffer.getInt
    int
  }

  implicit def quint64()(implicit parser: Parser): Long = parser.buffer.getLong

  def utf8()(implicit parser: Parser): String = {
    //      if (byteCount <= nextByte) {
    //        log.debug("nextString when byteCount is {} and nextByte is {}", byteCount, nextByte)
    //        return ""
    //      }
    val strSize: Int = quint32()
    if (strSize == 0xffffffff) return "" // Return an empty rather than null
    val sBytes = new Array[Byte](strSize)
    parser.buffer.get(sBytes)

    new String(sBytes, "UTF-8")
  }

  val offsetToYear = 2457763

  /**
   * @see https://doc.qt.io/qt-6/qdatetime.html#details
   * @return
   */
  def qDateTime()(implicit parser: Parser): QDateTime = {

      val qtDay: Long = quint64() // julian day See
      val localTime: LocalTime = qtime()
      val timespec: Byte = qint8()
      logger.trace("qtYear: {} localTime: {} timespec: {} ", qtDay, localTime, timespec)

      import java.time.LocalDate
      import java.time.temporal.JulianFields
      val localDate1 = LocalDate.MIN.`with`(JulianFields.JULIAN_DAY, qtDay)
      localDate1
      val localDateTime = LocalDateTime.of(localDate1, localTime)
      logger.info("localDateTime: {}", localDateTime.toString)
      val instant = localDateTime.toInstant(ZoneOffset.UTC)
      logger.trace("localDateTime: {} instant: {}", localDateTime.toString, instant.toString)
      instant

    //        throw new NotImplementedError() //todo

  }

  def bool()(implicit parser: Parser): Boolean = {
    parser.buffer.get() != 0
  }

  def float()(implicit parser: Parser): Double = {
    parser.buffer.getDouble
  }

  def qint8()(implicit parser: Parser): Byte = {
    parser.buffer.get()
  }

  def qtime()(implicit parser: Parser): LocalTime = {
    val ms = quint32()
    val ns = NANOSECONDS.convert(ms, MILLISECONDS)
    LocalTime.ofNanoOfDay(ns)
  }
}
