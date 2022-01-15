package com.meteofrance

import com.meteofrance.exceptions.NullHttpResponseException
import com.typesafe.scalalogging.Logger

import java.time.Instant

object Controller {
  val logger = Logger("Controller")

  /**
   * Execute the request to fetch data and return it as an Array of bytes.
   * If the token is invalid, a new one is generated.
   *
   * @param url URL of the endpoint to make a request to
   *
   * @return    The raw data of the response body as an array of bytes
   */
  @throws (classOf[NullHttpResponseException])
  def requestTo(url: String): Array[Byte] = {
    if (!Token.isStillValid()) {
      Token.renew()
    } else {
      val remain = Token.end - Instant.now.getEpochSecond
    }

    val headers: List[(String, String)] = List(
      ("Authorization", "Bearer " + Token.getValue()),
      ("Accept", "application/octet-stream")
    )
    val httpRequest: HttpRequest = new HttpRequest(url, headers, null);
    var responseBody: Array[Byte] = httpRequest.callAPI()

    if (responseBody == null) {
      logger.error("Response boby of request to " + url + " is null")
      throw new NullHttpResponseException("Response Body of request to " + url + " is null")
    } else {
      return responseBody
    }
  }
}
