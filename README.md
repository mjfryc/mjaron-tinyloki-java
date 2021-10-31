# mjaron-tinyloki-java

Tiny implementation of Loki client compatible with Java 1.8 without any external dependencies.

It implements JSON variant of [Loki API](https://grafana.com/docs/loki/latest/api/#post-lokiapiv1push).

[![Java CI with Gradle](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle.yml/badge.svg)](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle.yml)
[![Gradle Package](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle-publish.yml)

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
