package com.workingbit.echo.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import static java.net.HttpURLConnection.HTTP_CREATED;
import static java.net.HttpURLConnection.HTTP_OK;

@NoArgsConstructor
@Data
public class Answer {

  int statusCode;
  private Payload body;
  private MessageResponse message;

  @JsonCreator
  private Answer(@JsonProperty("body") Payload body, @JsonProperty("message") MessageResponse message) {
    this.body = body;
    this.message = message;
  }

  public static Answer ok(Payload body) {
    return new Answer(body, MessageResponse.ok())
        .statusCode(HTTP_OK);
  }

  public static Answer created(Payload body) {
    return new Answer(body, MessageResponse.created())
        .statusCode(HTTP_CREATED);
  }

  public static Answer error(int statusCode, String message) {
    return new Answer(null, MessageResponse.error(statusCode, message))
        .statusCode(statusCode);
  }

  public int getStatusCode() {
    return statusCode;
  }

  public void setStatusCode(int statusCode) {
    this.statusCode = statusCode;
  }

  public Answer statusCode(int statusCode) {
    setStatusCode(statusCode);
    return this;
  }
}