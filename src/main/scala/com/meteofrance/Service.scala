package com.meteofrance

import com.typesafe.scalalogging.Logger

import scala.collection.mutable.ListBuffer

/**
 * Manage received data and parse GRIB to Dataframe
 */
object Service {
  val logger = Logger("Service")
  var locations: ListBuffer[(Double, Double)] = null

  /**
   * Check if the given string is a valid GRIB file by checking if the first 4 bytes are "GRIB".
   * This is not the best way to check as the rest can be full jam binary data but it's a simple
   * and easy way. Normally the Météo France server may return only GRIB data on our calls.
   * 
   * @param str The string to test
   * @return    Boolean indicating if the string represents a GRIB file
   */
  def isGRIB(str: String): Boolean = {
    return str.substring(0, Math.min(str.length(), 4)).equals("GRIB")
  }
}
