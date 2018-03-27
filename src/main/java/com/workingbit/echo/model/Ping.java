package com.workingbit.echo.model;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Aleksey Popryaduhin on 23:25 06/10/2017.
 */
@JsonTypeName("ping")
@AllArgsConstructor
@Data
public class Ping implements Payload {

  private String ping;
}
