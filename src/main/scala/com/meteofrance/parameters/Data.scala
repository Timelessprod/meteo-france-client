package com.meteofrance.parameters

import scala.io.Source

object Data {
  val COVERAGES_BASE = Source.fromFile("coverages.txt").getLines().toArray

  // Coverages indexes which need "Ground or water surface" property
  val needGows: Array[Int] = Array(0, 1, 2, 5, 9, 10, 13, 14, 15, 16, 17, 18, 19, 20, 32)
  val gows: String = "__GROUND_OR_WATER_SURFACE"

  // Coverages indexes which need "Specific height level above ground"
  val needShlag: Array[Int] = Array(3, 6, 7, 11, 21, 24, 26, 28, 29, 33, 35, 36, 41, 42)
  val shlag: String = "__SPECIFIC_HEIGHT_LEVEL_ABOVE_GROUND"

  // Coverages indexes which need "Isobaric surface"
  val needIs: Array[Int] = Array(4, 8, 12, 23, 25, 27, 30, 31, 34, 37, 40, 43, 46)
  val is: String = "__ISOBARIC_SURFACE"

  // Coverages indexes which need "Potential vorticity surface 1500"
  val needPvs1500: Array[Int] = Array(38, 44)
  val pvs1500: String = "__POTENTIAL_VORTICITY_SURFACE_1500"

  // Coverages indexes which need "Potential vorticity surface 2000"
  val needPvs2000: Array[Int] = Array(39, 45)
  val pvs2000: String = "__POTENTIAL_VORTICITY_SURFACE_2000"

  // Coverages indexes which need "Mean sea level"
  val needMsl: Array[Int] = Array(22)
  val msl: String = "__MEAN_SEA_LEVEL"

  // Durations of some coverages (H: hour, D: day)
  val durations3d: Array[String] = Array("T3H", "T6H", "T9H", "T12H", "T18H", "1D", "2D", "3D")
  val durations4d: Array[String] = Array("T3H", "T6H", "T9H", "T12H", "T18H", "1D", "2D", "3D", "4D")

  val needDuration3d: Array[Int] = Array(9, 10)
  val needDuration4d: Array[Int] = Array(5, 18, 19)
}