package com.wa9nnn.multicasttool.util

import play.api.libs.json._

import scala.reflect.ClassTag


object JsonFormatUtils {
  // author: Marius Soutier  - https://stackoverflow.com/questions/21737019/create-reads-writes-for-java-enum-without-field
  def javaEnumFormat[E <: Enum[E] : ClassTag]: Format[E] = new Format[E] {
    override def reads(json: JsValue): JsResult[E] = json.validate[String] match {
      case JsSuccess(value, _) => try {
        val clazz = implicitly[ClassTag[E]].runtimeClass.asInstanceOf[Class[E]]
        JsSuccess(Enum.valueOf(clazz, value))
      } catch {
        case _: IllegalArgumentException => JsError("enumeration.unknown.value")
      }
      case JsError(_) => JsError("enumeration.expected.string")
    }

    override def writes(o: E): JsValue = JsString(o.toString)
  }
}