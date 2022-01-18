package com.meteofrance.exceptions

/**
 * Occurs when exceeding the maximum authorized iteration to fetch data or a new token.
 */
final case class ExceedingHttpRequestErrorThresholdException(private val message: String = "",
                                                             private val cause: Throwable = None.orNull)
  extends Exception(message, cause)
