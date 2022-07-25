package com.wa9nnn.multicasttool.multicast

import com.typesafe.scalalogging.LazyLogging
import play.api.libs.json.Json
import scalafx.collections.ObservableBuffer

import java.net.{DatagramPacket, InetAddress, MulticastSocket}
import java.util.concurrent.atomic.AtomicLong
import scala.collection.concurrent.TrieMap

class Multicast(multicastGroup: InetAddress, port: Int) extends LazyLogging {
  private val us = InetAddress.getLocalHost.getHostName
  private val nodeMap = new TrieMap[String, NodeStats]()

  val nodes: ObservableBuffer[NodeStats] = ObservableBuffer[NodeStats]()
  //  (
  //    new NodeStats(Message(InetAddress.getLocalHost, 42))
  //  )

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
      val payloadBytes = bufferBytes.take(recv.getLength)
      val sData = new String(bufferBytes)

      val message: UdpMessage = Json.parse(payloadBytes).as[UdpMessage]
      logger.debug("Received: {}", message)
      val host = message.host
      val nodeStats = nodeMap.getOrElseUpdate(host, {
        val ns =  NodeStats(message)
        nodes.addOne(ns)
        ns
      })
      nodeStats.add(message)


      //        if (recv.getAddress != localHost)
      //        logger.info(s"Multicast: addr:${recv.getAddress} => ${localHost.getHostName}:${recv.getPort} message: $sData")
    } while (true)

  }).start()

  val sn = new AtomicLong()

  def send(): Unit = {
    val message = UdpMessage(us, sn.incrementAndGet())
    val bytes = Json.toJson(message).toString().getBytes

    //      val message = s"multicast Message: $sn".getBytes()
    val datagramPacket = new DatagramPacket(bytes, bytes.length, multicastGroup, port)
    multicastSocket.send(datagramPacket)
    logger.debug("Sent: {}", message)
  }
}