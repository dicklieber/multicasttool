package com.wa9nnn.multicasttool.wsjt

import com.typesafe.scalalogging.LazyLogging

import java.net.{DatagramPacket, InetAddress, MulticastSocket}
import java.util.concurrent.ArrayBlockingQueue
import scala.util.{Failure, Success}

object UDP extends App with LazyLogging {
  private val queue = new ArrayBlockingQueue[Message](100)
  val multicastAddress: InetAddress = InetAddress.getByName("224.0.0.2")
  val sign: Long = 0xadbccbda
  new UDP(multicastAddress, 2237, queue)
  while (true) {
    val qMessage: Message = queue.take()
    logger.info(Option(qMessage).toString)
  }
}

class UDP(multicastGroup: InetAddress, port: Int, queue: ArrayBlockingQueue[Message]) extends LazyLogging {
  private val us = InetAddress.getLocalHost.getHostName

  var multicastSocket: MulticastSocket = new MulticastSocket(port);
  multicastSocket.setReuseAddress(true)
  multicastSocket.joinGroup(multicastGroup);

  var buf = new Array[Byte](1000);

  new Thread(() => {
    logger.info(s"Listening on ${multicastGroup.getHostName}:$port")
    do {
      val recv: DatagramPacket = new DatagramPacket(buf, buf.length);
      multicastSocket.receive(recv);
      val payloadBytes: Array[Byte] = recv.getData.take(recv.getLength)

      val triedMessage = Decoder(payloadBytes)

      triedMessage match {
        case Failure(exception) =>
          exception match {
            case DecodeException(mt, cause) =>
              println(s"mt: $mt :")
              cause.printStackTrace()
            case _ =>

          }
        case Success(message) =>
          println(message)
      }

    } while (true)

  }).start()


}