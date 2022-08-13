/*
 *   Copyright (C) 2022  Dick Lieber, WA9NNN
 *
 *   This program is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.wa9nnn.multicasttool.multicast

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.util.HostAndPort
import play.api.libs.json.Json

import java.net.{DatagramPacket, Inet4Address, InetAddress, InterfaceAddress, MulticastSocket, NetworkInterface, SocketException}
import scala.collection.concurrent.TrieMap
import scala.collection.mutable
import scala.jdk.StreamConverters._
import scala.jdk.CollectionConverters._
import scala.io.StdIn.readLine

object MInterface extends App with LazyLogging {

  val hostAndPort = HostAndPort("239.73.88.0", 1174)

  val multicastGroup = hostAndPort.toInetAddress

  var receiveSocket: MulticastSocket = new MulticastSocket(hostAndPort.port);
  receiveSocket.setReuseAddress(true)
  private val current: NetworkInterface = receiveSocket.getNetworkInterface
  private val iterfaces: List[NetworkInterface] = NetworkInterface.networkInterfaces().toScala(List)

  /*
    iterfaces.foreach { interface =>
      println(interface)
      val inetAddresses = interface.getInetAddresses
      println(s"\tinetAddresses: $inetAddresses")
      val list: mutable.Seq[InterfaceAddress] = interface.getInterfaceAddresses.asScala
      list.foreach { sif =>

        println(s"\t\tsif: $sif")

      }
    }
  */

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

  logger.info("Using {}", choosenInterface)
  var buf = new Array[Byte](1000);

//  private val nif: NetworkInterface = NetworkInterface.getByName("en7")
  receiveSocket.setNetworkInterface(choosenInterface)
  receiveSocket.joinGroup(hostAndPort.toSocketAddress, choosenInterface)
  private val interface: NetworkInterface = receiveSocket.getNetworkInterface
  private val localPort: Int = receiveSocket.getLocalPort

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

        //        message.copy(source = Option(recv.getAddress.toString))
        //        logger.debug("Received: {}", message)
        //        val host = message.host
        //        val nodeStats = nodeMap.getOrElseUpdate(host, {
        //          val ns = NodeStats(message)
        //          nodes.addOne(ns)
        //          ns
        //        })
        //        nodeStats.add(message)
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

  private val str: String = readLine()
  receiveSocket.leaveGroup(hostAndPort.toSocketAddress, choosenInterface)

}
