package com.workingbit.echo.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.workingbit.echo.common.AnswerDeserializer;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonDeserialize(using = AnswerDeserializer.class)
@NoArgsConstructor
@Data
public class Answer {

  private int code;
  private Object body;
  private String error;
  private Type type;

  public Answer(int code, String error) {
    this.code = code;
    this.error = error;
    this.type = Type.ERROR;
  }

  public Answer(int code, Object body, Type type) {
    this.code = code;
    this.body = body;
    this.type = type;
  }

  public static Answer ok(int code, Object body, Type classType) {
    return new Answer(code, body, classType);
  }
  
  public static Answer okEcho(int code, Object body) {
    return new Answer(code, body, Type.ECHO);
  }

  public static Answer okPong(int code, Object body) {
    return new Answer(code, body, Type.PONG);
  }

  public static Answer error(int code, String message) {
    return new Answer(code, message);
  }

  public enum Type {
    ECHO, PONG, ERROR
  }
}