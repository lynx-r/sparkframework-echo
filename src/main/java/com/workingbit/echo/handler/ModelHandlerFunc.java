package com.workingbit.echo.handler;


import com.workingbit.echo.model.Answer;
import com.workingbit.echo.model.Payload;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import static com.workingbit.echo.JsonUtils.dataToJson;
import static com.workingbit.echo.JsonUtils.jsonToData;

/**
 * Created by Aleksey Popryaduhin on 10:52 29/09/2017.
 */
@FunctionalInterface
public interface ModelHandlerFunc<T extends Payload> extends BaseHandlerFunc {

  default String handleRequest(Request request, Response response, Class<T> clazz) {
    String check = commonCheck(request);
    if (StringUtils.isNotBlank(check)) {
      return check;
    }
    String json = request.body();
    T data = jsonToData(json, clazz);
    Answer processed = process(data);
    response.status(processed.getStatusCode());
    return dataToJson(processed);
  }

  Answer process(T data);
}
