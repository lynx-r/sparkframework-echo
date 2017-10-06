package com.workingbit.echo.common;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.workingbit.echo.model.Answer;
import com.workingbit.echo.model.Echo;
import com.workingbit.echo.model.Pong;

import java.io.IOException;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;

/**
 * Created by Aleksey Popryaduhin on 20:56 30/09/2017.
 */
public class AnswerDeserializer extends JsonDeserializer<Answer> {

  private ObjectMapper mapper = new ObjectMapper();

  @Override
  public Answer deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    JsonNode jsonNode = p.getCodec().readTree(p);
    String classType = jsonNode.get("type").asText();
    JsonNode body = jsonNode.get("body");
    int code = jsonNode.get("code").asInt();
    Answer.Type type = Answer.Type.valueOf(classType);
    switch (type) {
      case ECHO: {
        return deserializeObject(code, body.toString(), Echo.class, Answer.Type.ECHO);
      }
      case PONG: {
        return deserializeObject(code, body.toString(), Pong.class, Answer.Type.PONG);
      }
      case ERROR: {
        return new Answer(code, jsonNode.get("error").asText());
      }
      default: {
        Answer answer = new Answer();
        answer.setCode(HTTP_BAD_REQUEST);
        answer.setError("Unable to deserialize " + p);
        answer.setType(Answer.Type.ERROR);
        return answer;
      }
    }
  }

  private <T> Answer deserializeObject(int code, String content, Class<T> valueType, Answer.Type type) throws IOException {
    T body = mapper.readValue(content, valueType);
    return new Answer(code, body, type);
  }
}
