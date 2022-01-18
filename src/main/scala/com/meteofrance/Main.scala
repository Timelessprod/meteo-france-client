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

  /**
   * Return the covered time period of the model passed as argument
   *
   * @param modelHour Integer representing the hour of the model corresponding
   *                  to the release date we want
   *
   * @return Number of hours covered by the model
   */
  def getTerm(modelHour: Int): Int = {
    var res: Int = 0

    if (modelHour == 0 || modelHour == 12) {
      res = 48
    } else if (modelHour == 3) {
      res = 45
    } else if (modelHour == 6 || modelHour == 18) {
      res = 42
    } else if (modelHour == 9 || modelHour == 15 || modelHour == 21) {
      res = 7
    }

    this.term = res
    return res
  }
}
