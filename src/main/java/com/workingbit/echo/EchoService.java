package com.workingbit.echo;


import com.workingbit.echo.model.Echo;
import com.workingbit.echo.model.Ping;
import com.workingbit.echo.model.Pong;

import java.util.Optional;

/**
 * Created by Aleksey Popryaduhin on 07:00 22/09/2017.
 */
public class EchoService {

  public Optional<Echo> echo(String echoString) {
    return Optional.of(new Echo(echoString));
  }

  public Optional<Pong> pong(Ping ping) {
    return Optional.of(new Pong(ping.getPing() + " PONG"));
  }
}
