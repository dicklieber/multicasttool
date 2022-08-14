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

package com.wa9nnn.multicasttool.broadcast

import java.net.{DatagramPacket, DatagramSocket, InetAddress}

object Broadcast extends App {

  val socket = new DatagramSocket();
  socket.setBroadcast(true);

  val buffer = "XYZZY".getBytes
  val address = InetAddress.getByName("255.255.255.255")
  val packet = new DatagramPacket(buffer, buffer.length, address, 4445)

  for (x <- 1 to 25) {
    socket.send(packet)
    println(".")
  }
  socket.close()

}
