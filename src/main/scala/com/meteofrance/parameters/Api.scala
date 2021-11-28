package com.meteofrance.parameters

import java.nio.charset.StandardCharsets
import java.util.Base64

object Api {
  // Validity time of the token (minus 1mn for security)
  val TOKEN_REFRESH_TIME: Long = 3540 // ms

  // User credentials
  val USER_ID: String = System.getenv("MF_USER_ID")
  val USER_KEY: String = System.getenv("MF_USER_KEY")
  val USER_CREDENTIALS: String = USER_ID + ":" + USER_KEY
  val KEY: String = Base64.getEncoder.encodeToString(USER_CREDENTIALS.getBytes(StandardCharsets.UTF_8))

  // URL to fetch a new token
  val TOKEN_URL: String = "https://portail-api.meteofrance.fr/token"

  // URL base for all endpoints the program need to fetch
  val BASE_URL: String = "https://public-api.meteofrance.fr/public"

  // URL for AROME 0.01 model
  val MODEL_AROME_001: String = "/arome/1.0/wcs/MF-NWP-HIGHRES-AROME-001-FRANCE-WCS"

  // URL for ARPEGE 0.25 Globe
  val MODEL_ARPEGE_025: String = "/arpege/1.0/wcs/MF-NWP-GLOBAL-ARPEGE-025-GLOBE-WCS/GetCoverage"

  // Common endpoint to fetch data
  val ENDPOINT: String = "/GetCoverage?SERVICE=WCS&VERSION=2.0.1"

  // Data type the program need to collect (GRIB)
  val GRIB_TYPE: String = "&format=application%2Fwmo-grib"

  // Full URL to use for AROME 0.01
  val URL_AROME_001: String = BASE_URL + MODEL_AROME_001 + ENDPOINT + GRIB_TYPE
  val URL_ARPEGE_025: String = BASE_URL + MODEL_ARPEGE_025 + ENDPOINT + GRIB_TYPE
}
