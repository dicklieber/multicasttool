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

package com.wa9nnn.multicasttool.scalafx.wsjt

import com.wa9nnn.multicasttool.multicast.NodeStats
import com.wa9nnn.multicasttool.scalafx.App.multicast
import com.wa9nnn.multicasttool.wsjt.MessageType
import com.wa9nnn.multicasttool.wsjt.messages.Message
import scalafx.beans.property.{ReadOnlyProperty, ReadOnlyStringWrapper}
import scalafx.scene.control.{Label, TableColumn, TableView}
import scalafx.scene.layout.BorderPane

class MessagesPane extends BorderPane {

  val messageCircularBuffer = new MessageCircularBuffer(25)

  top = new Label("Multicast Stats Pane")
  center = new TableView[Message](messageCircularBuffer) {
    columns ++= List(
      new TableColumn[Message, String] {
        text = "Stamp"
        cellValueFactory = { m =>
          ReadOnlyStringWrapper(m.value.stamp.toString)
        }
        prefWidth = 180
      },
      new TableColumn[Message, String] {
        text = "Type"
        cellValueFactory = { m: TableColumn.CellDataFeatures[Message, String] =>
          ReadOnlyStringWrapper(m.value.messageType.toString)
        }
        prefWidth = 125
      },
      new TableColumn[Message, String] {
        text = "Detail"
        cellValueFactory = { m: TableColumn.CellDataFeatures[Message, String] =>
          ReadOnlyStringWrapper(m.value.detail)
        }
        prefWidth = 125
      }
      //          new TableColumn[NodeStats, String]() {
      //            text = "LastMessage"
      //            cellValueFactory = _.value.lastMessage
      //            prefWidth = 250
      //          }
    )
  }


  def add(message: Message): Unit = {
    messageCircularBuffer.add(message)
  }
}
