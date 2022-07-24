package com.wa9nnn.multicasttool.wsjt

import com.wa9nnn.multicasttool.util.JsonFormatUtils

object MessageTypeFormat {
  implicit val mfF = JsonFormatUtils.javaEnumFormat[MessageType]

}
