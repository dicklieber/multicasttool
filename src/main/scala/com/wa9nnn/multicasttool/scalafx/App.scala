package com.wa9nnn.multicasttool.scalafx

import com.wa9nnn.multicasttool.multicast.{Multicast, NodeStats}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.scene.control.{TableColumn, TableView}

import java.net.InetAddress
import java.util.{Timer, TimerTask}

object App extends JFXApp {
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
          new TableColumn[NodeStats, Long] {
            text = "Count"
            cellValueFactory = _.value.totalReceived
            prefWidth = 125
          },
          new TableColumn[NodeStats, Long] {
            text = "S/N"
            cellValueFactory = _.value.lastSn
            prefWidth = 125
          }
          //          new TableColumn[NodeStats, String]() {
          //            text = "LastMessage"
          //            cellValueFactory = _.value.lastMessage
          //            prefWidth = 250
          //          }
        )
      }
    }
  }
}
