package com.meteofrance

import com.meteofrance.exceptions.ExceedingHttpRequestErrorThresholdException
import com.typesafe.scalalogging.Logger
import com.meteofrance.parameters.Api
import com.meteofrance.parameters.Global
import okhttp3.{FormBody, Headers, OkHttpClient, Request, Response}
import com.fasterxml.jackson.databind.{ObjectMapper, JsonNode}
import com.fasterxml.jackson.module.scala._
//import com.fasterxml.jackson.module.scala.experimental.ScalaObjectMapper
import java.time.Instant
import java.util.concurrent.TimeUnit;

/**
 * Class holding and managing the API's authentification token
 *
 * @param token  The token as a private string
 * @param start  Starting moment of validity of the token in seconds since Epoch
 * @param end    Ending moment of validity of the token in seconds since Epoch
 * @param client OkHttpClient which manage the requests to renew the token when needed
 */
object Token {
  val logger = Logger("Token")
  private[this] var token: String = ""
  var start: Long = 0
  var end: Long = 0
  val client: OkHttpClient = new OkHttpClient.Builder().followRedirects(false)
                                                       .followSslRedirects(false)
                                                       .connectTimeout(Global.TIMEOUT, TimeUnit.MILLISECONDS)
                                                       .readTimeout(Global.TIMEOUT, TimeUnit.MILLISECONDS)
                                                       .writeTimeout(Global.TIMEOUT, TimeUnit.MILLISECONDS)
                                                       .callTimeout(3 * Global.TIMEOUT, TimeUnit.MILLISECONDS)
                                                       .build()

  /**
   * Return the token's value in order to put it in the collect requests
   *
   * @return The token attribute
   */
  def getValue() : String = {
    return this.token
  }

  /**
   * Check if the token is still valid based on the start & end attributes
   *
   * @return A boolean telling wether or not the token is still valid
   */
  def isStillValid() : Boolean = {
    return (this.token != "" && Instant.now.getEpochSecond >= this.start
                             && Instant.now.getEpochSecond < this.end)
  }

  /**
   * Build the request from the headers, body and url given as attribute or argument
   *
   * @param headers The list of headers to put in the request
   * @param data    The data to put in the request as body
   *
   * @return The request as a OkHttp Request object
   */
  def getRequest(headers: List[(String, String)],
                 data: List[(String, String)]) : Request = {
    logger.info("Building request to renew the token")
    val header = new Headers.Builder()

    for (couple <- headers) {
      header.add(couple._1, couple._2)
    }

    val body = new FormBody.Builder()

    for (couple <- data) {
      body.add(couple._1, couple._2)
    }

    return new Request.Builder().url(Api.TOKEN_URL)
      .headers(header.build())
      .post(body.build())
      .build()
  }

  /**
   * Execute the POST request with given class attributes and global parameters
   * and return the server's response as a string
   *
   * @param headers The list of headers to put in the request
   * @param data    The data to put in the request as body
   *
   * @return The server's response to the call as a string
   */
  def executeRequest(headers: List[(String, String)],
                     data: List[(String, String)]) : String = {
    val result = try {
      // Building request
      val request = getRequest(headers, data)
      logger.info("Request to renew the token is built")

      // Executing the request
      var response: Response = try {
        this.client.newCall(request).execute()
      } catch {
        case e: Throwable => {
          logger.error("Failed to renew token");
          e.printStackTrace;
          null
        }
      }

      // Check for errors in the response
      if (response == null || !response.isSuccessful) {
        logger.error("Request to renew token returned code : HTTP" + response.code)

        var i = 1

        // Try again couple times
        while (i <= Global.FAIL_THRESHOLD && response != null && !response.isSuccessful) {
          Thread.sleep(Global.SLEEPING_TIME)

          response = try {
            this.client.newCall(request).execute()
          } catch {
            case e: Throwable => {
              logger.error("Request attempt n°" + i + " to renew token failed");
              e.printStackTrace;
              null
            }
          }

          i += 1
        }

        // If the response is valid we continue, if not we quit
        if (response.isSuccessful) {
          logger.info("Request to renew token succeed after " + i + " calls")
        } else {
          logger.error("Request to renew token failed more than maximum authorized times")
          throw new ExceedingHttpRequestErrorThresholdException()
        }
      } else {
        logger.info("Request to renew token succeed (HTTP 200)")
      }

      val responseBody: String = response.body.string()
      response.close()

      return responseBody
    } catch {
      case e: Throwable => {
        logger.error("An error occured while fetching data from the API");
        e.printStackTrace;
        null
      }
    }

    return result
  }

  /**
   * Check if the current token is valid and if not, a new token will be fetch
   * from the server
   */
  @throws (classOf[ExceedingHttpRequestErrorThresholdException])
  def renew() : Unit = {
    if (!this.isStillValid()) {
      // Create the request
      val tokenRequestHeaderList: List[(String,String)] = List(("Authorization", "Basic " + Api.KEY))
      val tokenRequestDataList: List[(String,String)] = List(("grant_type", "client_credentials"))

      // Execute the request
      val response = executeRequest(tokenRequestHeaderList, tokenRequestDataList)

      if (response == null) {
        logger.error("Failed to renew the token")
        throw new ExceedingHttpRequestErrorThresholdException()
      }

      // Update token starting oment of validity
      this.start = Instant.now.getEpochSecond

      // Parse the JSON response
      val objectMapper: ObjectMapper = new ObjectMapper()
      val jsonNode: JsonNode = objectMapper.readTree(response)

      if (jsonNode.has("error")) {
        logger.error("Unable to update token : JSON response contain an error label")
      } else {
        this.token = jsonNode.get("access_token").asText()
        Api.TOKEN_REFRESH_TIME = jsonNode.get("expires_in").asLong() - 60 // Security of 1 minute for network lag
        this.end = this.start + Api.TOKEN_REFRESH_TIME
      }
    }
  }
}
