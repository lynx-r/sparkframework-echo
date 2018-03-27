package com.workingbit.echo;

import com.workingbit.echo.handler.ModelHandlerFunc;
import com.workingbit.echo.handler.QueryParamsHandlerFunc;
import com.workingbit.echo.model.Answer;
import com.workingbit.echo.model.Ping;
import spark.Route;

import static com.workingbit.echo.EchoApplication.echoService;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

/**
 * Created by Aleksey Popryaduhin on 13:58 27/09/2017.
 */
public class EchoController {

  public static Route echo = (req, res) ->
      ((QueryParamsHandlerFunc) echoQuery ->
          echoService
              .echo(echoQuery.value("echo"))
              .map(Answer::ok)
              .orElse(Answer.error(HTTP_BAD_REQUEST, ErrorMessages.UNABLE_TO_ECHO + req.body()))
      ).handleRequest(req, res);

  public static Route pong = (req, res) ->
      ((ModelHandlerFunc<Ping>) pongRequest ->
          echoService
              .pong(pongRequest)
              .map(Answer::ok)
              .orElse(Answer.error(HTTP_BAD_REQUEST, ErrorMessages.UNABLE_TO_PONG + req.body()))
      ).handleRequest(req, res, Ping.class);
}
