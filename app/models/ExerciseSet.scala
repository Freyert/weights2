package models

import play.api.libs.json._


case class ExerciseSet(id: Long, name: String, date: java.sql.Date)

object ExerciseSet {
  implicit val exerciseSetFormat = Json.format[ExerciseSet]
}