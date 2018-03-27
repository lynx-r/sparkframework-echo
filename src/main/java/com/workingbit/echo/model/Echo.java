package com.workingbit.echo.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Aleksey Popryaduhin on 23:09 06/10/2017.
 */
@JsonTypeName("echo")
@AllArgsConstructor
@Data
public class Echo implements Payload {

  private String echo;
}
