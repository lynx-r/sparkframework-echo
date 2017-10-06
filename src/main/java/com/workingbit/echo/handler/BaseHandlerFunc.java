package com.workingbit.echo.handler;

import com.workingbit.echo.common.ErrorMessages;
import com.workingbit.echo.model.Answer;
import spark.Request;
import spark.utils.StringUtils;

import static com.workingbit.echo.util.JsonUtils.dataToJson;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

/**
 * Created by Aleksey Popryaduhin on 16:37 01/10/2017.
 */
public interface BaseHandlerFunc {

  default String checkSign(Request request) {
    try {
      String apiKey = System.getenv("API_KEY");
      if (StringUtils.isBlank(apiKey)) {
        System.out.println("Ignore api key");
        return null;
      }
      return dataToJson(Answer.error(HTTP_BAD_REQUEST, ErrorMessages.MALFORMED_REQUEST));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return dataToJson(Answer.error(HTTP_BAD_REQUEST, ErrorMessages.MALFORMED_REQUEST));
  }
}
