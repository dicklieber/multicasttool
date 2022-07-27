package com.wa9nnn.multicasttool.wsjt

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.multicasttool.wsjt.messages.Message

import java.net.{DatagramPacket, InetAddress, MulticastSocket}
import scala.util.{Failure, Success}

object WSJTTestFixture extends App with LazyLogging {
  val multicastAddress: InetAddress = InetAddress.getByName("224.0.0.2")
  val sign: Long = 0xadbccbda
  new WSJT(multicastAddress, 2237, (message: Message) =>
    logger.info(message.toString)
  )
}

/**
 * Connects to WSJT multicast socket.
 * Listens for messages and distributes them to the [[callback]] function.
 *
 * @param multicastGroup    as set in WSJT-X Settings > Reporting > UDP Server
 * @param port              as set in WSJT-X Settings > Reporting > UDP Server port number.
 * @param callback          that will receive messages. Note this is invoked on the thread that listens to the multicast socket.
 */
class WSJT(multicastGroup: InetAddress, port: Int, callback: Message => Unit) extends LazyLogging {
  var multicastSocket: MulticastSocket = new MulticastSocket(port)
  multicastSocket.setReuseAddress(true)
  multicastSocket.joinGroup(multicastGroup)


  new Thread(() => {
    logger.info(s"Listening on ${multicastGroup.getHostName}:$port")
    val buf = new Array[Byte](1000)
    do {
      val datagramPacket: DatagramPacket = new DatagramPacket(buf, buf.length)
      multicastSocket.receive(datagramPacket)
      val payloadBytes: Array[Byte] = datagramPacket.getData.take(datagramPacket.getLength)

      val triedMessage = Decoder(payloadBytes)

      triedMessage match {
        case Failure(exception) =>
          exception match {
            case DecodeException(mt, cause) =>
              println(s"mt: $mt :")
              cause.printStackTrace()
            case e: NoHandlerException =>
              logger.error(e.getMessage)
            case e: Throwable =>
              logger.error("Unexpected Exception", e)
          }
        case Success(message) =>
          try {
            callback(message)
          } catch {
            case e: Throwable =>
              logger.error("Callback threw", e)
          }
      }

    } while (true)

  }).start()


}

trait MessageListener {
  def newMessage(message: Message): Unit
}
