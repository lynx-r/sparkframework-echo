package com.workingbit.echo.controller;

import com.despegar.http.client.GetMethod;
import com.despegar.http.client.HttpClientException;
import com.despegar.http.client.HttpResponse;
import com.despegar.http.client.PostMethod;
import com.despegar.sparkjava.test.SparkServer;
import com.workingbit.echo.EchoApplication;
import com.workingbit.echo.model.Answer;
import com.workingbit.echo.model.Echo;
import com.workingbit.echo.model.Ping;
import com.workingbit.echo.model.Pong;
import org.junit.ClassRule;
import org.junit.Test;
import spark.servlet.SparkApplication;

import java.util.Random;

import static com.workingbit.echo.JsonUtils.dataToJson;
import static com.workingbit.echo.JsonUtils.jsonToData;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by Aleksey Popryaduhin on 17:56 30/09/2017.
 */
public class EchoControllerTest {

  private static String echoUrl = "/api/v1";
  private static Integer randomPort = 1000 + new Random().nextInt(60000);

  public static class BoardBoxControllerTestSparkApplication implements SparkApplication {

    @Override
    public void init() {
      EchoApplication.start();
    }
  }

  @ClassRule
  public static SparkServer<BoardBoxControllerTestSparkApplication> testServer = new SparkServer<>(BoardBoxControllerTestSparkApplication.class, randomPort);

  @Test
  public void should_echo() throws HttpClientException {
    String echoMsg = "echo";
    Echo echo = (Echo) get("/echo?echo" + "=" + echoMsg).getBody();
    assertEquals(echoMsg, echo.getEcho());
  }

  @Test
  public void should_pong() throws HttpClientException {
    Pong pong = (Pong) post("/ping", new Ping("PING")).getBody();
    assertEquals("PING PONG", pong.getPong());
  }

  private Answer post(String path, Object payload) throws HttpClientException {
    PostMethod resp = testServer.post(echoUrl + path, dataToJson(payload), false);
    HttpResponse execute = testServer.execute(resp);
    return jsonToData(new String(execute.body()), Answer.class);
  }

  private Answer get(String params) throws HttpClientException {
    GetMethod resp = testServer.get(echoUrl + "/" + params, false);
    HttpResponse execute = testServer.execute(resp);
    return jsonToData(new String(execute.body()), Answer.class);
  }
}