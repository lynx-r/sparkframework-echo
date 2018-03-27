package com.workingbit.echo.handler;

import spark.Request;

/**
 * Created by Aleksey Popryaduhin on 16:37 01/10/2017.
 */
public interface BaseHandlerFunc {

  default String commonHeadersCheck(Request request) {
    // do your smart check here
    return null;
  }
}
