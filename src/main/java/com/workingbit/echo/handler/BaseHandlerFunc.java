package com.workingbit.echo.handler;

import com.workingbit.echo.common.ErrorMessages;
import com.workingbit.echo.model.Answer;
import com.workingbit.echo.util.CryptoUtils;
import spark.Request;
import spark.utils.StringUtils;

import static com.workingbit.echo.util.JsonUtils.dataToJson;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

/**
 * Created by Aleksey Popryaduhin on 16:37 01/10/2017.
 */
public interface BaseHandlerFunc {

  default String checkSign(Request request) {
    String sign = request.headers("sign");
    String signRequest = request.headers("request-sign");
    System.out.println(String.format("SIGN %s, SIGN_REQUEST %s", sign, signRequest));
    try {
      String vkApiKeyEnv = System.getenv("VK_API_KEY_ENV");
      if (StringUtils.isBlank(vkApiKeyEnv)) {
        System.out.println("Ignore vk sign key");
        return null;
      }
      String sig = CryptoUtils.encode(vkApiKeyEnv, signRequest);
      if (!sig.equals(sign)) {
        return dataToJson(Answer.error(HTTP_BAD_REQUEST, ErrorMessages.MALFORMED_REQUEST));
      }
    } catch (Exception e) {
      return dataToJson(Answer.error(HTTP_BAD_REQUEST, ErrorMessages.MALFORMED_REQUEST));
    }
    return null;
  }
}
