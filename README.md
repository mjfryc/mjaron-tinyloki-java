# tinyloki-java

Tiny implementation of Loki client for Java 1.8

[![Java CI with Gradle](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle.yml/badge.svg)](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle.yml)

## API description

HTTP sender requires http URL, optionally basic authentication credentials may be set.

Below sample use case:

```java
public class Sample {
    public static void main(String[] args) {
        LogController logController = new LogController(new JsonLogCollector(),
                new LogSender(LogSenderSettings.create()
                        .setUrl("http://localhost/loki/api/v1/push")
                        .setUser("user")
                        .setPassword("pass"))).start();
        Map<String, String> labels = new TreeMap<>();
        labels.put("level", "INFO");
        labels.put("host", "ZEUS");
        ILogStream stream = logController.createStream(labels);
        stream.log(System.currentTimeMillis(), "Hello world.");
        logController.softStop().
        waitForStop();
    }
}
```
