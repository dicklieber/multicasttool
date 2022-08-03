package com.wa9nnn.multicasttool.scalafx

import com.wa9nnn.multicasttool.multicast.{Multicast, NodeStats}
import com.wa9nnn.multicasttool.scalafx.wsjt.{MessagesPane, StatusPane, WSJTThing}
import com.wa9nnn.util.HostAndPort
import scalafx.Includes._
import scalafx.application.JFXApp3
import scalafx.scene.Scene
import scalafx.scene.control.{Label, TableColumn, TableView}
import scalafx.scene.layout.{BorderPane, HBox}

import java.net.InetAddress
import java.util.{Timer, TimerTask}

object App extends JFXApp3 {
  val multicastAddress: InetAddress = InetAddress.getByName("239.73.88.0")
  val timer = new Timer("SendTimer", true)
  private val multicast: Multicast = new Multicast(multicastAddress, 1174)


  override def start(): Unit = {
    val multicastPane = new BorderPane {
      top = new Label("Multicast Stats Pane")
      center = new TableView[NodeStats](multicast.nodes) {
        columns ++= List(
          new TableColumn[NodeStats, String] {
            text = "Host"
            cellValueFactory = _.value.host
            prefWidth = 180
          },
          new TableColumn[NodeStats, Long] {
            text = "Count"
            cellValueFactory = _.value.totalReceived
            prefWidth = 35
          },
          new TableColumn[NodeStats, Long] {
            text = "S/N"
            cellValueFactory = _.value.lastSn
            prefWidth = 35
          },
          new TableColumn[NodeStats, String] {
            text = "Since"
            cellValueFactory = _.value.since
            prefWidth = 100
          }
          //          new TableColumn[NodeStats, String]() {
          //            text = "LastMessage"
          //            cellValueFactory = _.value.lastMessage
          //            prefWidth = 250
          //          }
        )
      }
    }

    val statusPane = new StatusPane()
    val messagesPane = new MessagesPane
    val wsjt = new WSJTThing(HostAndPort("224.0.0.2:2237", 2237), 50, statusPane, messagesPane)

    timer.scheduleAtFixedRate(new TimerTask {
      override def run(): Unit = {
        multicast.send()
      }
    }, 10L, 1000L)

    stage = new JFXApp3.PrimaryStage {
      title = "Multicast Stats"
      scene = new Scene {
        val cssUrl: String = getClass.getResource("/scalafx.css").toExternalForm
        stylesheets += cssUrl


        content = new HBox(multicastPane,
          statusPane, messagesPane)
      }
    }
  }

}
