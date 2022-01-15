package com.meteofrance

import com.meteofrance.exceptions.NullHttpResponseException
import com.meteofrance.parameters.Global.TIMEOUT
import com.typesafe.scalalogging.Logger
import okhttp3.{Headers, OkHttpClient, Request, Response}

import java.util.concurrent.TimeUnit

/**
 * Class in charge of executing HTTP request to the Météo France's API server to fetch data.
 *
 * @param url     URL adress of the API endpoint to call
 * @param headers Content of the request header as a list of (key, value)
 */
class HttpRequest(val url: String, val headers: List[(String, String)], val data: List[(String, String)]) {
  val logger = Logger("HttpRequest")
  val client: OkHttpClient = new OkHttpClient.Builder().followRedirects(false)
                                                       .followSslRedirects(false)
                                                       .connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                                                       .readTimeout(TIMEOUT, TimeUnit.MILLISECONDS)
                                                       .build()

  /**
   * Build the request to execute using class's attributes.
   *
   * @return The reauest to execute
   */
  def getRequest() : Request = {
    val headers = new Headers.Builder

    if (headers == null)
      return null

    for (header <- this.headers) {
      headers.add(header._1, header._2)
    }

    logger.info("HEader of request to " + this.url + " is built")

    return new Request.Builder().url(this.url)
                                .headers(headers.build)
                                .build
  }

  def getResponse(request: Request) : Array[Byte] = {
    var response: Response = null

    try {
      logger.info("Calling the API on " + this.url)
      response = this.client.newCall(request)
                     .execute()

      if (response == null)
        throw new NullHttpResponseException()

      logger.info("API call returned a valid response")
    } catch {
      case e: NullHttpResponseException => {
        logger.warn("API call returned a null response");
        return null
      }
      case e: Throwable => {
        logger.warn("Method client.newcall.execute() has thrown an exception")
        return null
      }
    }

    if (response.isSuccessful) {
      logger.info("API response is successful")
      var body: Array[Byte] = null;

      try {
        body = response.body.bytes()
        logger.info("API response's bytes collected")
      } catch {
        case e: Throwable => {
          logger.warn("An error occured while getting bytes of the response")
          return null
        }
      }

      try {
        response.close()
        logger.info("Response closed")
        return body
      } catch {
        case e: Throwable => {
          logger.warn("An error occured while closing the reponse")
          return null
        }
      }
    } else {
      logger.warn("API call failed and returned HTTP error code " + response.code + " - \'" + response.message + "\'")
      response.close()
      return null
    }
  }
}
