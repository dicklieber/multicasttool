package com.wa9nnn.multicasttool.multicast

import com.typesafe.scalalogging.LazyLogging
//import com.wa9nnn.multicasttool.multicast.MInterface.{iterfaces, logger}
import com.wa9nnn.util.HostAndPort
import org.scalafx.extras.onFX
import play.api.libs.json.Json
import scalafx.collections.ObservableBuffer

import java.net._
import java.util.concurrent.atomic.AtomicLong
import java.util.{Timer, TimerTask}
import scala.collection.concurrent.TrieMap
import scala.jdk.CollectionConverters.EnumerationHasAsScala
import scala.jdk.StreamConverters.StreamHasToScala

class Multicast(hostAndPort: HostAndPort) extends LazyLogging {

  private val broadcastHost = HostAndPort("255.255.255.255", 7311)

  val localHost: InetAddress = InetAddress.getLocalHost
  val us: String = localHost.getHostName
  private val nodeMap = new TrieMap[String, NodeStats]()
  private val tickTimer = new Timer("PropertyCellTimer", true)
  tickTimer.scheduleAtFixedRate(new TimerTask {
    override def run(): Unit = {
      onFX {
        nodeMap.values.foreach(_.tick())
      }
    }
  }, 5, 750)


  val multicastGroup: InetAddress = hostAndPort.toInetAddress

  val sn = new AtomicLong()


  val nodes: ObservableBuffer[NodeStats] = ObservableBuffer[NodeStats]()

  var receiveSocket: MulticastSocket = new MulticastSocket(hostAndPort.port);
  receiveSocket.setReuseAddress(true)

  private val iterfaces: List[NetworkInterface] = NetworkInterface.networkInterfaces().toScala(List)

  val ipAddresses: List[(InetAddress, NetworkInterface)] = {
    (for {
      interface <- iterfaces
      inetAddress <- interface.getInetAddresses.asScala
      if inetAddress.isInstanceOf[Inet4Address]
    } yield {
      inetAddress -> interface
    }
      ).sortBy(_._1.isLoopbackAddress)
  }
  ipAddresses.foreach { ipa =>
    println(s"${ipa._2}: ${ipa._1}")
  }

  ipAddresses.length match {
    case 2 =>
      logger.info(s"Only one IPV4")
    case 1 =>
      logger.info(s"Only $ipAddresses.head available, probably loopback.")
    case 0 =>
      throw new IllegalStateException("No IPV4 IP addresses!")
    case x =>
      logger.error("Multiple ipv4 available! Using first.")
  }
  private val choosenInterface: NetworkInterface = ipAddresses.head._2


  receiveSocket.joinGroup(hostAndPort.toSocketAddress, choosenInterface)

  def shutdown(): Unit = {
    logger.info("cancel send timer")
    tickTimer.cancel()

    logger.info("Leaving group: {}", hostAndPort)
    try {
      sendtimer.cancel()
      sendSocket.close()

      receiveSocket.leaveGroup(hostAndPort.toSocketAddress, choosenInterface)
      receiveSocket.close()
    } catch {
      case e: Throwable =>
        logger.error("Error leaving group: {}", e, multicastGroup)
    }
  }

  var buf = new Array[Byte](1000);

  new Thread(() => {
    logger.info(s"multicast listening on  on ${
      multicastGroup.getHostName
    }:${hostAndPort.port}")
    var ongoing = true
    do {
      try {
        val recv: DatagramPacket = new DatagramPacket(buf, buf.length);
        receiveSocket.receive(recv);
        val bufferBytes: Array[Byte] = recv.getData
        val payloadBytes = bufferBytes.take(recv.getLength)

        val message: UdpMessage = Json.parse(payloadBytes).as[UdpMessage]

        message.copy(source = Option(recv.getAddress.toString))
        logger.debug("Received: {}", message)
        val host = message.host
        val nodeStats = nodeMap.getOrElseUpdate(message.protocol + ":" + host, {
          val ns = NodeStats(message)
          nodes.addOne(ns)
          ns
        })
        nodeStats.add(message)
      } catch {
        case e: SocketException =>
          logger.debug("Receive socket closed.")
          ongoing = false
        case e: Throwable =>
          logger.error("Exception Receive loop", e)
      }


      //        if (recv.getAddress != localHost)
      //        logger.info(s"Multicast: addr:${recv.getAddress} => ${localHost.getHostName}:${recv.getPort} multicastMessage: $sData")
    } while (ongoing)

  })

  new Thread(() => {
    logger.info(s"Broadcast on ${
      multicastGroup.getHostName
    }:${hostAndPort.port}")

    val socket = new DatagramSocket(broadcastHost.port)

    var ongoing = true
    do {
      try {
        val recv: DatagramPacket = new DatagramPacket(buf, buf.length);
        socket.receive(recv);
        val bufferBytes: Array[Byte] = recv.getData
        val payloadBytes = bufferBytes.take(recv.getLength)

        val message: UdpMessage = Json.parse(payloadBytes).as[UdpMessage]

        message.copy(source = Option(recv.getAddress.toString))
        logger.debug("Received: {}", message)
        val host = message.host
        val nodeStats = nodeMap.getOrElseUpdate(message.protocol + ":" + host, {
          val ns = NodeStats(message)
          nodes.addOne(ns)
          ns
        })
        nodeStats.add(message)
      } catch {
        case e: SocketException =>
          logger.debug("Receive socket closed.")
          ongoing = false
        case e: Throwable =>
          logger.error("Exception Receive loop", e)
      }


      //        if (recv.getAddress != localHost)
      //        logger.info(s"Multicast: addr:${recv.getAddress} => ${localHost.getHostName}:${recv.getPort} multicastMessage: $sData")
    } while (ongoing)

  }).start()

  val sendSocket = new DatagramSocket()
  sendSocket.setReuseAddress(true)
  sendSocket.isBound

  val sendtimer = new Timer("SendTimer", true)

  sendtimer.scheduleAtFixedRate(new TimerTask {
    override def run(): Unit = {
      send()
    }
  }, 10L, 1000L)

  def send(): Unit = {

    val multicastMessage = UdpMessage("M", us, sn.incrementAndGet())
    val multicastBytes: Array[Byte] = Json.toJson(multicastMessage).toString().getBytes

    val datagramPacket = new DatagramPacket(multicastBytes, multicastBytes.length, multicastGroup, hostAndPort.port)
    try {
      sendSocket.send(datagramPacket)
      logger.debug("Sent: {}", multicastMessage)
    } catch {
      case e: SocketException =>
        logger.debug("Send socket closed")
    }
    val broadcastMessage = UdpMessage("B", us, sn.incrementAndGet())
    val broadcatBytes: Array[Byte] = Json.toJson(broadcastMessage).toString().getBytes
    val broadcastPackets = new DatagramPacket(broadcatBytes, broadcatBytes.length, broadcastHost.toInetAddress, broadcastHost.port)
    sendSocket.send(broadcastPackets)

  }
}