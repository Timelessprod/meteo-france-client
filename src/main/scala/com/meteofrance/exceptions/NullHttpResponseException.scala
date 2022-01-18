package com.meteofrance.exceptions

/**
 * Occurs when the server returns a null response body or when the request fails.
 */
final case class NullHttpResponseException(private val message: String = "",
                                           private val cause: Throwable = None.orNull)
  extends Exception(message, cause)
