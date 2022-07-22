package com.wa9nnn.multicasttool.wsjt

import com.typesafe.scalalogging.LazyLogging

import java.net.{DatagramPacket, InetAddress, MulticastSocket}
import java.util.concurrent.ArrayBlockingQueue

object UDP extends App with LazyLogging {
  private val queue = new ArrayBlockingQueue[QMessage](100)
  val multicastAddress: InetAddress = InetAddress.getByName("224.0.0.2")
  val sign: Long = 0xadbccbda
  new UDP(multicastAddress, 2237, queue)
  while (true) {
    val qMessage: QMessage = queue.take()
    logger.info(Option(qMessage).toString)
  }
}

class UDP(multicastGroup: InetAddress, port: Int, queue: ArrayBlockingQueue[QMessage]) extends LazyLogging {
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
      val bufferBytes: Array[Byte] = recv.getData

      val decoder = new MessageDecoder(bufferBytes, recv.getLength)
      Option(decoder.decode()).foreach(qMessage =>
        queue.put(qMessage)
      )




      //        if (recv.getAddress != localHost)
      //        logger.info(s"Multicast: addr:${recv.getAddress} => ${localHost.getHostName}:${recv.getPort} message: $sData")
    } while (true)

  }).start()


}