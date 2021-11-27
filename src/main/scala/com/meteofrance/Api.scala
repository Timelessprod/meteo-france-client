package com.meteofrance

object Api {
  // Validity time of the token (minus 1mn for security)
  val TOKEN_REFRESH_TIME: Long = 3540 // ms

  // User credentials
  val USER_ID: String = System.getenv("MF_USER_ID")
  val USER_KEY: String = System.getenv("MF_USER_KEY")

  // URL to fetch a new token
  val TOKEN_URL: String = "https://portail-api.meteofrance.fr/token"

  // URL base for all endpoints the program need to fetch
  val BASE_URL: String = "https://public-api.meteofrance.fr/public/"
}