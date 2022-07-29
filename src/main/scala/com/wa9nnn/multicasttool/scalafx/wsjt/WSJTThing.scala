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

import com.typesafe.scalalogging.LazyLogging
import com.wa9nnn.multicasttool.wsjt.WSJT
import com.wa9nnn.multicasttool.wsjt.messages.{HeartbeatMessage, Message, StatusMessage}
import com.wa9nnn.util.HostAndPort
import scalafx.beans.property.ObjectProperty

import java.net.InetAddress
import java.util.concurrent.atomic.AtomicInteger

class WSJTThing(hostAndPort: HostAndPort, capacity: Int, statusPane: StatusPane, messagesPane: MessagesPane) extends LazyLogging {

  val messageCount = new AtomicInteger()
  val groupAddress: InetAddress = InetAddress.getByName(hostAndPort.host)
  var lastHeartbeat: Option[HeartbeatMessage] = None

  private def handleHeartbeat(heartbeatMessage: HeartbeatMessage): Unit =

    lastHeartbeat = Option(heartbeatMessage)

  def handleStatus(statusMessage: StatusMessage): Unit = {
    statusPane.add(statusMessage)
  }

  new WSJT(groupAddress, hostAndPort.port, (message: Message) => {

    message match {
      case hb: HeartbeatMessage => handleHeartbeat(hb)
      case sm: StatusMessage => handleStatus(sm)

      case other =>
        messagesPane.add(other)
    }
    messageCount.incrementAndGet()

  })


}
