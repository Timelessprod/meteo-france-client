package com.meteofrance.parameters

import java.text.SimpleDateFormat
import java.util.{Calendar, TimeZone}
import scala.io.Source

object Data {
  val UTC: TimeZone = Timezone.getTimeZone("UTC")
  TimeZone.setDefault(UTC)
  val IsoDateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
  
  val COVERAGES_BASE: Array[String] = Source.fromFile("coverages.txt").getLines().toArray
  var COVERAGES: Array[String] = Array()

  // Coverages indexes which need "Ground or water surface" property
  val needGows: Array[Int] = Array(0, 1, 2, 5, 9, 10, 13, 14, 15, 16, 17, 18, 19, 20, 32)
  val gows: String = "GROUND_OR_WATER_SURFACE"

  // Coverages indexes which need "Specific height level above ground"
  val needShlag: Array[Int] = Array(3, 6, 7, 11, 21, 24, 26, 28, 29, 33, 35, 36, 41, 42)
  val shlag: String = "SPECIFIC_HEIGHT_LEVEL_ABOVE_GROUND"

  // Coverages indexes which need "Isobaric surface"
  val needIs: Array[Int] = Array(4, 8, 12, 23, 25, 27, 30, 31, 34, 37, 40, 43, 46)
  val is: String = "ISOBARIC_SURFACE"

  // Coverages indexes which need "Potential vorticity surface 1500"
  val needPvs1500: Array[Int] = Array(38, 44)
  val pvs1500: String = "POTENTIAL_VORTICITY_SURFACE_1500"

  // Coverages indexes which need "Potential vorticity surface 2000"
  val needPvs2000: Array[Int] = Array(39, 45)
  val pvs2000: String = "POTENTIAL_VORTICITY_SURFACE_2000"

  // Coverages indexes which need "Mean sea level"
  val needMsl: Array[Int] = Array(22)
  val msl: String = "MEAN_SEA_LEVEL"
  
  val needProperties: Array[Array[Int]] = Array(needGows, needShlag, needIs, needPvs1500, needPvs2000, needMsl)
  val properties: Array[String] = Array(gows, shlag, is, pvs1500, pvs2000, msl)

  // Durations of some coverages (H: hour, D: day)
  val needDuration3d: Array[Int] = Array(9, 10)
  val durations3d: Array[String] = Array("T3H", "T6H", "T9H", "T12H", "T18H", "1D", "2D", "3D")
  
  val needDuration4d: Array[Int] = Array(5, 18, 19)
  val durations4d: Array[String] = Array("T3H", "T6H", "T9H", "T12H", "T18H", "1D", "2D", "3D", "4D")
  
  val needDurations: Array[Array[Int]] = Array(needDuration3d, needDuration4d)
  val durations: Array[Array[String]] = Array(durations3d, durations4d)
  
  def needDuration(i: Int) : Boolean = {
    for (j <- 0 to this.needDurations.length) {
      if (this.needDurations(j) contains i) {
        return true
      }
    }
    return false
  }
  
  def generateCoverages(modelDate: Calendar) : Array[String] = {
    var coverages: Array[String] = Array()
    val model: String = this.IsoDateFormat.format(modelDate.getTime)
    
    for (i <- 0 to this.COVERAGES_BASE.length) {
      var cov: String = this.COVERAGES_BASE(i)
      
      for (j <- 0 to this.needProperties.length) {
        if (needProperties(j) contains i) {
          cov = cov + "__" + properties(j)
        }
      }
      
      cov = cov + "___" + model
      
      if (needDuration(i)) {
        for (j <- 0 to this.needDurations.length) {
          if (needDurations(j) contains i) {
            val covD: String = cov + "_P"
            
            for (duration <- this.durations(j)) {
              val covDD: String = covD + duration
              coverages :+= covDD
            }
          }
        }
      } else {
        coverages :+= cov
      }
    }
    
    this.COVERAGES = coverages
    return coverages
  }
}
