# Создание простого RESTful API с Java Spark

## Что вы изучите

Вы изучите как можно определять обобщенные контроллеры с помощью функциональных интерфейсов Java 8. [Пример кода на GitHub](https://github.com/lynx-r/sparkframework-echo.git).

## EchoApplication.java

Это класс который связывает ваше приложение воедино. Когда вы откроете этот класс вы сразу должны понять как всё работает:

```java
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
```

## Статические зависимости?

Статические зависимости это не то что вы привыкли видеть в `Java`, но может быть статика лучше чем внедряемые зависимости, когда речь идет о контроллерах в веб приложениях. Тем более, что в нашем микросервисе, который выполняет только одну функцию, сервисов будет не много. Из преимуществ, нужно заметить, что отказа от DI ускоряет запуск приложения, а писать юнит тесты для одного или двух сервисов можно и без DI.

## Path и Controller.field

В классе `Path.java` я держу точки входа в REST API в виде констант. В этом приложении только два обработчика запросов, которые помещены в один контроллер `EchoController`:

```java
public class EchoController {

  public static Route echo = (req, res) ->
      ((QueryParamsHandlerFunc) echoQuery ->
          echoService
              .echo(echoQuery.value("echo"))
              .map(Answer::ok)
              .orElse(Answer.error(HTTP_BAD_REQUEST, ErrorMessages.UNABLE_TO_ECHO + req.body()))
      ).handleRequest(req, res);

  public static Route pong = (req, res) ->
      ((ModelHandlerFunc<Ping>) pongRequest ->
          echoService
              .pong(pongRequest)
              .map(Answer::ok)
              .orElse(Answer.error(HTTP_BAD_REQUEST, ErrorMessages.UNABLE_TO_PONG + req.body()))
      ).handleRequest(req, res, Ping.class);
}
```

Первый обработчик, есть лямбда функция, телом которой является функциональный интерфейс `QueryParamsHandlerFunc`. Входным параметром этого интерфейса являются данные GET запроса - словарь с частью запросов из ссылки. (Например, `/echo?echo=message`). В теле этого интерфейса выполняется вызов нашего сервиса обработчика. Т.е. сервис получает уже собранный объект и никак не зависит от контроллера, что облегчает тестирование. Сервис возвращает `Optional`, который отображается на класс `Answer`, который создает ответ. В случае ошибки возвращается класс `Answer` с кодом ошибки и сообщением об ошибке. Этот интерфейс имеет метод `QueryParamsHandlerFunc::handleRequest`, в который передаются запрос и ответ из контроллера-обработчика `Func::handlerRequest(req, res)`.

Второй обработчик, возвращает результат, который формирует примерно такой же интерфейс как и выше, только в качестве шаблонного параметра указан класс, наследующий интерфейс `Payload`. Обработка этого запроса ни чем не отличается от описанного выше. Разница лишь в том, что метод `ModelHandlerFunc::handleRequest` этого интерфейса получает класс нашего `Payload` в качестве параметра `ModelHandlerFunc<Ping>`.

## QueryParamsHandlerFunc.java

Это функциональный интерфейс, наследующий базовый интерфейс, в который вынесены общие для обработчика GET запросов действия. В этом интерфейсе определен метод по умолчанию QueryParamsHandlerFunc::handleRequest, который принимает в качестве входных параметров объекты запроса и ответа. Выполняет какие-то проверки, например, заголовков, вызывая метод базового интерфейса BaseHandlerFunc::commonCheck(request). Далее, берёт из запроса словарь запроса (/echo?echo=message), передает их в метод определенный в интерфейсе QueryParamsHandlerFunc::process, после обработки запроса указывает код ответа и сериализует этот ответ в Json.

```java
@FunctionalInterface
public interface QueryParamsHandlerFunc  extends BaseHandlerFunc {

  default String handleRequest(Request request, Response response) {
    String check = commonCheck(request);
    if (StringUtils.isNotBlank(check)) {
      return check;
    }
    QueryParamsMap queryParamsMap = request.queryMap();
    Answer processed = process(queryParamsMap);
    response.status(processed.getCode());
    return dataToJson(processed);
  }

  Answer process(QueryParamsMap data);
}
```

## ModelHandlerFunc.java

Этот интерфейс работает так же как и описанный выше, с тем лишь отличаем, что он обрабатывает POST запросы. Конвертированный класс, так же как и при обработке параметров запроса передается в метод интерфейса `ModelHandlerFunc::process`.

```java
@FunctionalInterface
public interface ModelHandlerFunc<T extends Payload> extends BaseHandlerFunc {

  default String handleRequest(Request request, Response response, Class<T> clazz) {
    String check = commonCheck(request);
    if (StringUtils.isNotBlank(check)) {
      return check;
    }
    String json = request.body();
    T data = jsonToData(json, clazz);
    Answer processed = process(data);
    response.status(processed.getCode());
    return dataToJson(processed);
  }

  Answer process(T data);
}
```

## BaseHandlerFunc.java

Это интерфейс, который агрегирует в себе общие методы.

```java
public interface BaseHandlerFunc {

  default String commonCheck(Request request) {
    // do your smart check here
    return null;
  }
}

```

## Jackson Polymorphic Type Handling Annotations

Для сереализации в JSON класса Answer и его нагрузки был применен полиморфизм с аннтоацией `@JsonTypeInfo`. Подробнее по ссылкам.

## Тест контроллера

Для тестирования контроллера воспользуемся библиотекой [Spark Test](https://github.com/despegar/spark-test). Пример кода тестирование.

```java
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
```

## Ссылки

* [Creating a library website with login and multiple languages](http://sparkjava.com/tutorials/application-structure)
* [Jackson Annotation Examples](http://www.baeldung.com/jackson-annotations)
* [Java Spark Test](https://github.com/despegar/spark-test)
* [Jackson Polymorphic Type Handling Annotations](https://fasterxml.github.io/jackson-annotations/javadoc/2.2.0/com/fasterxml/jackson/annotation/JsonTypeInfo.html)
