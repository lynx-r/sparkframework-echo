# Spark Framework пример варианта определения контроллеров веб-приложения

## Что вы изучите

Вы изучите как можно определять обобщенные контроллеры с помощью функциональных интерфейсов Java 8. Пример кода на GitHub https://github.com/helloalleo/spark-example.

### Структура проекта

    EchoApplication.java

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

Статические зависимости это не то что вы привыкли видеть в Java, но может быть статика лучше чем внедряемые зависимости, когда речь идет о контроллерах в веб приложениях. Тем более, что в нашем микросервисе, который выполняет только одну функцию, сервисов будет не много. Из преимуществ, нужно заметить, что отказа от DI ускоряет запуск приложения, а писать юнит тесты для одного или двух сервисов можно и без DI.

## Path и Controller.field

В классе Path.java я держу точки входа в REST API в виде констант. В этом приложении только два обработчика запросов, которые помещены в один контроллер EchoController. Так как этот контроллер не большой, приведу весь его код:

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

Первый обработчик, есть лямбда функция, телом которой является функциональный интерфейс QueryParamsHandlerFunc. Входным параметром этого интерфейса является мап с частью запросов из ссылки, т.е. /echo?echo=message. В теле этого интерфейса выполняется вызов нашего сервиса. Т.е. сервис получает уже обработанные параметры и никак не зависит от контроллера, что облегчает тестирование. Сервис возвращает Optional, который мапится на класс ответа Answer, который содержит тело ответа, enum типа класса тела ответа и HTTP код ответа. В случае ошибки возвращается класс Answer с кодом ошибки и сообщением об ошибке. Этот интерфейс имеет метод handleRequest, в который передаются запрос и ответ из контроллера-обработчика Func::handlerRequest(req, res).

Второй обработчик, возвращает результат, который формирует примерно такой же интерфейс как и выше, только в качестве шаблонного параметра указан класс, наследующий интерфейс Payload. Обработка этого запроса ни чем не отличается от описанного выше. Разница лишь в том, что метод handleRequest этого интерфейса получает класс нашего Payload в качестве параметра ModelHandlerFunc<Ping>.

    QueryParamsHandlerFunc.java

Это функциональный интерфейс, наследующий базовый интерфейс, в который вынесены общие для всех обработчиков действия. Их мы рассмотрим позднее. В этом интерфейсе определен метод по умолчанию handleRequest, который принимает в качестве входных параметров объекты запроса и ответа, выполняет проверку сигнатуры, вызывая метод базового класса checkSign(request). Далее, берёт из запроса параметры запроса (/echo?echo=message), передает их в метод интерфейса process, после обработки запроса указывает код ответа и сериализует этот ответ в Json.

```java
@FunctionalInterface
public interface QueryParamsHandlerFunc  extends BaseHandlerFunc{

  default String handleRequest(Request request, Response response) {
    String check = commonHeadersCheck(request);
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

    ModelHandlerFunc.java

Этот интерфейс работает так же как и описанный выше, с тем лишь отличаем, что из запроса берётся тело запроса в формате Json и конвертируется в класс, наследующий Payload. Конвертированный класс, так же как и при обработке параметров запроса передается в метод интерфейса process.

```java
@FunctionalInterface
public interface ModelHandlerFunc<T extends Payload> extends BaseHandlerFunc {

  default String handleRequest(Request request, Response response, Class<T> clazz) {
    String check = commonHeadersCheck(request);
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

    BaseHandlerFunc.java

Это интерфейс, который агрегирует в себе общие методы. Для примера я взял метод проверки сигнатуры запроса из приложения в vk.com.

```java
public interface BaseHandlerFunc {

  default String commonHeadersCheck(Request request) {
    // do your smart check here
    return null;
  }
}

```

# Тест контроллера

Для тестирования контроллера воспользуемся библиотекой https://github.com/despegar/spark-test. Пример кода тестирование.

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

# Заключение

В этой статье мы рассмотрели альтернативный вариант создания контроллера для вебфреймворка Spark. Как решают ту же задачу на официальном сайте можно посмотреть по ссылке http://sparkjava.com/tutorials/application-structure

От себя добавлю, причину по которой я выбрал Spark framework, а не Spring Boot. Дело в том, что я для тестов разворачиваю свои приложения на AWS Lambda. Для этого я пользуюсь этой библиотекой https://github.com/awslabs/aws-serverless-java-container, которая оборачивает приложение Spring, Spark или Jersey в понятную для AWS Lambda обертку. А так как лямбда функция работает не постоянно, а через пять минут прерывает свою работу, то при каждом новом запросе приходится ждать пока запуститься сервер. Spring Boot даже без Hibernate запускался долго, порядка 8 секунд плюс время на обработку запроса по протоколу HTTP, в то время как Spark запускается быстрее порядка 3 секунд. Конечно, еще можно взять JavaScript фреймворк, но это уже другая история.

# Вывод

Скорее всего, вам не понадобится SparkFramework, т.к. у вас есть Spring, но если вам нужна простая и быстрая альтернатива Node.js для деплоя на микросервис, то SparkFramework вам подойдёт.