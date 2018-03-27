package com.workingbit.echo.handler;

import com.workingbit.echo.model.Answer;
import spark.QueryParamsMap;
import spark.Request;
import spark.Response;
import spark.utils.StringUtils;

import static com.workingbit.echo.JsonUtils.dataToJson;


/**
 * Created by Aleksey Popryaduhin on 16:27 01/10/2017.
 */
@FunctionalInterface
public interface QueryParamsHandlerFunc  extends BaseHandlerFunc{

  default String handleRequest(Request request, Response response) {
    String check = commonHeadersCheck(request);
    if (StringUtils.isNotBlank(check)) {
      return check;
    }
    QueryParamsMap queryParamsMap = request.queryMap();
    Answer processed = process(queryParamsMap);
    response.status(processed.getStatusCode());
    return dataToJson(processed);
  }

  Answer process(QueryParamsMap data);
}
