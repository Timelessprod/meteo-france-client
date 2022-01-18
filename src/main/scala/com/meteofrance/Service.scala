package com.meteofrance

import com.typesafe.scalalogging.Logger
import org.apache.commons.io.FileUtils
import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.functions.{col, udf}
import com.ph.grib2tools._
import com.ph.grib2tools.grib2file._
import com.ph.grib2tools.grib2file.griddefinition.GridDefinitionTemplate30
import com.ph.grib2tools.grib2file.productdefinition.ProductDefinitionTemplate40

import java.io.{ByteArrayInputStream, File, FileInputStream, InputStream}
import scala.collection.mutable.ListBuffer

/**
 * Manage received data and parse GRIB to Dataframe
 */
object Service {
  val logger: Logger = Logger("Service")
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

  def readGRIB(bytes: Array[Byte], df: DataFrame, colName: String): DataFrame = {
    try {

      logger.info("Starting to parse the GRIB data")

      val path: String = "mf-data.grib"
      val file: File = new File(path)
      FileUtils.writeByteArrayToFile(file, bytes)

      logger.info("Temporary file holding GRIB data has been successfully written")

      val input = new FileInputStream(path)

      // Defines how many GRIB2 file structures shall be skipped when reading the GRIB2 file. This
      // is useful since some organizations put several GRIB2 file structures in one file.
      val numSkip: Int = 0

      // Id of the grid within the GRIB2 file, since GRIB2 files can contain several grids
      val gridId: Int = 0

      val grib: RandomAccessGribFile = new RandomAccessGribFile("meteo-france", path)
      val input2: InputStream = new ByteArrayInputStream(bytes)
      grib.importFromStream(input2, numSkip)

      logger.info("GRIB data is being loaded and ready for processing")

      val value = (lat: Double, lon: Double) => {
        grib.getValueAtLocation(gridId, lat, lon).asInstanceOf[Double]
      }

      val valueUDF = udf(value)
      val newDf = df.withColumn(colName, valueUDF(col("Latitude"), col("Longitude")))

      if (!file.delete()) {
        logger.error("Cannot delete temporary file !")
      }
      logger.info("End of parsing. GRIB file has been deleted")

      return newDf

    } catch {
      case e: Throwable => e.printStackTrace()
        return df
    }
  }
}
