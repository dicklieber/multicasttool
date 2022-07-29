
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

import com.wa9nnn.multicasttool.scalafx.GridOfControls
import com.wa9nnn.multicasttool.scalafx.wsjt.StatusPane.set
import com.wa9nnn.multicasttool.wsjt.messages.{Message, StatusMessage}
import org.scalafx.extras.onFX
import scalafx.beans.property.{IntegerProperty, StringProperty}
import scalafx.scene.control.Label
import scalafx.scene.layout.GridPane

import java.util.concurrent.atomic.AtomicInteger

/**
 * Shows the current [[StatusMessage]]
 * and a bit of metadata.
 */
class StatusPane extends GridOfControls() {
  val countSource = new AtomicInteger()

  styleClass = Seq("statPane")
  private val stamp = add("stamp", "...")
  private val id1 = add("id", "...")
  private val dialFrequency = add("dialFrequency", "...")
  private val mode = add("mode", "...")
  private val dxCall: StringProperty = add("dxCall", "...")
  private val report = add("report", "...")
  private val txMode = add("txMode", "...")
  private val txEnabled = add("txEnabled", "...")
  private val transmitting = add("transmitting", "...")
  private val decoding = add("decoding", "...")
  private val rxDf = add("rxDf", "...")
  private val txDf = add("txDf", "...")
  private val deCall = add("deCall", "...")
  private val deGrid = add("deGrid", "...")
  private val dxGrid = add("dxGrid", "...")
  private val txWatachDog = add("txWatachDog", "...")
  private val subMode = add("subMode", "...")
  private val fastMode = add("fastMode", "...")
  private val specialOpMode = add("specialOpMode", "...")
  private val frequencyTolerence = add("frequencyTolerence", "...")
  private val trPeriod = add("trPeriod", "...")
  private val configName = add("configName", "...")
  private val txMessage = add("txMessage", "...")
  private val count = add("Count", "...")


  def add(message: StatusMessage): Unit = {
    onFX {
      set(stamp, message.stamp)
      set(id1, message.id)
      set(dialFrequency, f"${message.dialFrequency / 1000000.0}%,.4f Mhz")
      set(mode, message.mode)
      set(dxCall, message.dxCall)
      set(report, message.report)
      set(txMode, message.txMode)
      set(txEnabled, message.txEnabled)
      set(transmitting, message.transmitting)
      set(decoding, message.decoding)
      set(rxDf, message.rxDf)
      set(txDf, message.txDf)
      set(deCall, message.deCall)
      set(deGrid, message.deGrid)
      set(dxGrid, message.dxGrid)
      set(txWatachDog, message.txWatachDog)
      set(subMode, message.subMode)
      set(fastMode, message.fastMode)
      set(specialOpMode, message.specialOpMode)
      set(frequencyTolerence, message.frequencyTolerence)
      set(trPeriod, message.trPeriod)
      set(configName, message.configName)
      set(txMessage, message.txMessage)
      set(count, countSource.incrementAndGet())
    }
  }
}

object StatusPane {
  def set(p: StringProperty, value: Any): Unit = {
    val cell = com.wa9nnn.util.tableui.Cell(value)
    p.value = cell.value
  }
}
