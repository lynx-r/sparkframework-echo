package com.workingbit.echo.handler;


import com.workingbit.echo.model.Answer;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import java.util.Map;

import static com.workingbit.echo.JsonUtils.dataToJson;

/**
 * Created by Aleksey Popryaduhin on 10:52 29/09/2017.
 */
@FunctionalInterface
public interface ParamsHandlerFunc extends BaseHandlerFunc{

  default String handleRequest(Request request, Response response) {
    String check = commonCheck(request);
    if (StringUtils.isNotBlank(check)) {
      return check;
    }
    Map<String, String> id = request.params();
    Answer processed = process(id);
    response.status(processed.getStatusCode());
    return dataToJson(processed);
  }

  Answer process(Map<String, String> data);
}
