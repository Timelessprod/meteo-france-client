package com.meteofrance.exceptions

/**
 * Occurs when the received body holding data isn't in GRIB2 format. This is the only supported format of data.
 * Any other format may indicate corrupted data.
 */
final case class InvalidResponseBodyTypeException(private val message: String = "",
                                                  private val cause: Throwable = None.orNull)
  extends Exception(message, cause)