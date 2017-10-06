package com.workingbit.echo.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Aleksey Popryaduhin on 23:25 06/10/2017.
 */
@AllArgsConstructor
@Data
public class Ping implements Payload {

  private String ping;
}
