package com.meteofrance.exceptions

final case class ExceedingHttpRequestErrorThresholdException(private val message: String = "",
                                                             private val cause: Throwable = None.orNull)
  extends Exception(message, cause)
