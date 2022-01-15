package com.meteofrance

import com.meteofrance.parameters.Global.TIMEOUT
import com.typesafe.scalalogging.Logger
import okhttp3.OkHttpClient

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
}
