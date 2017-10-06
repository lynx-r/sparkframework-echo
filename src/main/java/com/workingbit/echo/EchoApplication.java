package com.workingbit.echo;

import com.workingbit.echo.controller.EchoController;
import com.workingbit.echo.service.EchoService;
import com.workingbit.echo.util.JsonUtils;
import com.workingbit.echo.util.Path;
import org.apache.log4j.Logger;

import static spark.Spark.*;

public class EchoApplication {

  private static final Logger LOG = Logger.getLogger(EchoApplication.class);

  // Declare dependencies
  public static EchoService echoService;

  static {
    echoService = new EchoService();
  }

  public static void main(String[] args) {
    port(4567);
    start();
  }

  public static void start() {
    JsonUtils.registerModules();

    LOG.info("Initializing routes");
    establishRoutes();
  }

  private static void establishRoutes() {
    path("/api", () ->
        path("/v1", () -> {
          get(Path.ECHO, EchoController.echo);
          post(Path.PONG, EchoController.pong);
        })
    );
  }
}
