# tinyloki-java
Tiny implementation of Loki client for Java 1.8

## API description

HTTP sender requires http URL, optionally basic authentication credentials may be set.

Below sample use case:

```java
LogController logController = new LogController(new JsonLogCollector(),
        LogSenderSettings.create()
                .setUrl("http://localhost/loki/api/v1/push")
                .setUser("user")
                .setPassword("pass")).start();
Map<String, String> labels = new TreeMap<>();
labels.put("level", "INFO");
labels.put("host", "ZEUS");
ILogStream stream = logController.createStream(labels);

stream.log(System.currentTimeMillis(), "Hello world.");

// Optionally
logController.softStop().waitForStop();
```
