package com.meteofrance

import com.typesafe.scalalogging.Logger
import org.apache.spark.sql.DataFrame

import java.lang.Long
import java.text.SimpleDateFormat
import java.time.temporal.ChronoUnit
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

  /**
   * Return the date and time at which the model given as argument will be published
   *
   * @param modelHour Integer representing the hour of the model corresponding
   *                  to the release date we want
   *
   * @return The timestamp of the release we asked
   */
  def getRelease(modelHour: Int): Calendar = {
    val release: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    release.set(Calendar.MILLISECOND, 0)
    release.set(Calendar.SECOND, 0)

    if (modelHour > this.today.get(Calendar.HOUR_OF_DAY) && modelHour != 21) {
      release.add(Calendar.DAY_OF_YEAR, -1)
    }

    if (modelHour == 0) {
      release.set(Calendar.HOUR_OF_DAY, 2)
      release.set(Calendar.MINUTE, 45)
    } else if (modelHour == 3) {
      release.set(Calendar.HOUR_OF_DAY, 5)
      release.set(Calendar.MINUTE, 45)
    } else if (modelHour == 6) {
      release.set(Calendar.HOUR_OF_DAY, 11)
      release.set(Calendar.MINUTE, 5)
    } else if (modelHour == 9) {
      release.set(Calendar.HOUR_OF_DAY, 12)
      release.set(Calendar.MINUTE, 30)
    } else if (modelHour == 12) {
      release.set(Calendar.HOUR_OF_DAY, 15)
      release.set(Calendar.MINUTE, 45)
    } else if (modelHour == 15) {
      release.set(Calendar.HOUR_OF_DAY, 18)
      release.set(Calendar.MINUTE, 10)
    } else if (modelHour == 18) {
      release.set(Calendar.HOUR_OF_DAY, 22)
      release.set(Calendar.MINUTE, 45)
    } else if (modelHour == 21) {
      release.set(Calendar.HOUR_OF_DAY, 0)
      release.set(Calendar.MINUTE, 30)
    } else {
      return null
    }

    return release
  }

  /**
   * Define the timestamp of the model corresponding to the given hour as an integer
   *
   * @param modelHour Integer representing the hour of the model we want to set
   */
  def setModel(modelHour: Int): Unit = {
    val model: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))

    model.set(Calendar.MILLISECOND, 0)
    model.set(Calendar.SECOND, 0)
    model.set(Calendar.MINUTE, 0)

    if (modelHour > this.today.get(Calendar.HOUR_OF_DAY)) {
      model.add(Calendar.DAY_OF_YEAR, -1)
    }

    model.set(Calendar.HOUR_OF_DAY, modelHour)

    logger.info("Most recent available Model : " + ISOdf.format(model.getTime))

    this.modelDate = model
    this.term = getTerm(modelHour)
  }

  /**
   * Set the model according to the closest available model based on current time
   */
  def setModel(): Unit = {
    // Define release dates from 0h to 21h
    val releases: Array[Calendar] = 0.to(7).map(i => getRelease(i * 3)).toArray

    // Find the closest one (in the past)
    var min: Long = Long.MAX_VALUE
    var index: Int = 0

    for (i <- 0 to releases.size - 1) {
      var duration = ChronoUnit.SECONDS.between(releases(i).toInstant, this.today.toInstant)

      if (duration < 0) {
        duration += 86400
        releases(i).add(Calendar.DAY_OF_YEAR, -1)
      }

      if (duration < min) {
        min = duration
        index = i
      }
    }

    setModel(3 * index)
  }


}
