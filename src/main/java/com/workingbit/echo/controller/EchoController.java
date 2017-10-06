package com.workingbit.echo.controller;

import com.workingbit.echo.common.ErrorMessages;
import com.workingbit.echo.handler.ModelHandlerFunc;
import com.workingbit.echo.handler.QueryParamsHandlerFunc;
import com.workingbit.echo.model.Answer;
import com.workingbit.echo.model.Ping;
import spark.Route;

import static com.workingbit.echo.EchoApplication.echoService;
import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_GONE;

/**
 * Created by Aleksey Popryaduhin on 13:58 27/09/2017.
 */
public class EchoController {

  public static Route echo = (req, res) ->
      ((QueryParamsHandlerFunc) echoQuery ->
          echoService
              .echo(echoQuery.value("echo"))
              .map((echo) -> Answer.okEcho(HTTP_CREATED, echo))
              .orElse(Answer.error(HTTP_GONE, ErrorMessages.UNABLE_TO_ECHO + req.body()))
      ).handleRequest(req, res);

  public static Route pong = (req, res) ->
      ((ModelHandlerFunc<Ping>) pongRequest ->
          echoService
              .pong(pongRequest)
              .map((pong) -> Answer.okPong(HTTP_CREATED, pong))
              .orElse(Answer.error(HTTP_GONE, ErrorMessages.UNABLE_TO_PONG + req.body()))
      ).handleRequest(req, res, Ping.class);
}
