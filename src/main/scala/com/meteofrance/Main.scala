package com.meteofrance

import com.typesafe.scalalogging.Logger
import org.apache.spark.sql.DataFrame

import java.text.SimpleDateFormat
import java.util.{Calendar, TimeZone}

object Main {
  val logger: Logger = Logger("Main")
  var pdv: DataFrame = null

  val today: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
  val ISOdf: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")

  var modelDate: Calendar = null
  var term: Int = 0
}
