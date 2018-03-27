package com.workingbit.echo.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Aleksey Popryaduhin on 23:09 06/10/2017.
 */
@JsonTypeName("pong")
@AllArgsConstructor
@Data
public class Pong implements Payload {

  private String pong;
}
