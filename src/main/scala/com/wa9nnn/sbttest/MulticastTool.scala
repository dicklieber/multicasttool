package com.wa9nnn.sbttest

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.sbttest.Message.fmt
import play.api.libs.json.Json
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.beans.property.ObjectProperty
import scalafx.collections.ObservableBuffer
import scalafx.scene.Scene
import scalafx.scene.control.TableColumn._
import scalafx.scene.control.{TableColumn, TableView}

import java.net.{DatagramPacket, InetAddress, MulticastSocket}
import java.util.concurrent.atomic.AtomicLong
import java.util.{Timer, TimerTask}
import scala.collection.concurrent.TrieMap

object MulticastTool extends JFXApp {
  val multicastAddress: InetAddress = InetAddress.getByName("239.73.88.0")
  val timer = new Timer("SendTimer", true)
  private val multicast: Multicast = new Multicast(multicastAddress, 1174)

  timer.scheduleAtFixedRate(new TimerTask {
    override def run(): Unit = {
      multicast.send()
    }
  }, 10L, 1000L)


  stage = new PrimaryStage {
    title = "Multicast Stats"
    scene = new Scene {
      content = new TableView[NodeStats](multicast.nodes) {
        columns ++= List(
          new TableColumn[NodeStats, String] {
            text = "Host"
            cellValueFactory = _.value.host
            prefWidth = 180
          },
          new TableColumn[NodeStats, Int] {
            text = "Count"
            cellValueFactory = { value => {
              ObjectProperty[Int](value.value.totalReceived.get())
            }
            }
            prefWidth = 180
          },
          new TableColumn[NodeStats, String]() {
            text = "LastMessage"
            cellValueFactory = _.value.lastMessage
            prefWidth = 180
          }
        )
      }
    }
  }
}

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

      val message: Message = Json.parse(payloadBytes).as[Message]
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
    val message = Message(us, sn.getAndIncrement())
    val bytes = Json.toJson(message).toString().getBytes

    //      val message = s"multicast Message: $sn".getBytes()
    val datagramPacket = new DatagramPacket(bytes, bytes.length, multicastGroup, port)
    multicastSocket.send(datagramPacket)
    logger.debug("Sent: {}", message)
  }
}
