package com.wa9nnn.multicasttool.multicast

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.util.HostAndPort
import org.scalafx.extras.onFX
import play.api.libs.json.Json
import scalafx.collections.ObservableBuffer

import java.net._
import java.util.concurrent.atomic.AtomicLong
import java.util.{Timer, TimerTask}
import scala.collection.concurrent.TrieMap

class Multicast(hostAndPort: HostAndPort) extends LazyLogging {
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


  val multicastGroup = hostAndPort.toInetAddress

  val sn = new AtomicLong()


  val nodes: ObservableBuffer[NodeStats] = ObservableBuffer[NodeStats]()

  var receiveSocket: MulticastSocket = new MulticastSocket(hostAndPort.port);
  receiveSocket.setReuseAddress(true)
  val nif: NetworkInterface = NetworkInterface.getByName("en7")
  receiveSocket.setNetworkInterface(nif)

  receiveSocket.joinGroup(hostAndPort.toSocketAddress.getAddress)

  def shutdown(): Unit = {
    logger.debug("cancel send timer")
    tickTimer.cancel()

    logger.debug("Leaving group: {}", hostAndPort)
    try {
      sendtimer.cancel()
      sendSocket.close()

      receiveSocket.leaveGroup(multicastGroup)
      receiveSocket.close()
    } catch {
      case e: Throwable =>
        logger.error("Error leaving group: {}", e, multicastGroup)
    }
  }

  var buf = new Array[Byte](1000);

  new Thread(() => {
    logger.info(s"Listening on ${
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
        val nodeStats = nodeMap.getOrElseUpdate(host, {
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
      //        logger.info(s"Multicast: addr:${recv.getAddress} => ${localHost.getHostName}:${recv.getPort} message: $sData")
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

    val bound = sendSocket.isBound
    val connected = sendSocket.isConnected


    val message = UdpMessage(us, sn.incrementAndGet())
    val bytes = Json.toJson(message).toString().getBytes

    //      val message = s"multicast Message: $sn".getBytes()
    val datagramPacket = new DatagramPacket(bytes, bytes.length, multicastGroup, hostAndPort.port)
    try {
      sendSocket.send(datagramPacket)
      logger.debug("Sent: {}", message)
    } catch {
      case e: SocketException =>
        logger.debug("Send socket closed")
    }
  }
}