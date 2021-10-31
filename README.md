# mjaron-tinyloki-java

[![Java CI with Gradle](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle.yml/badge.svg)](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle.yml)
[![Gradle Package](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle-publish.yml)

Tiny implementation of Loki client compatible with Java 1.8 without any external dependencies.

* Implements JSON variant of [Loki API](https://grafana.com/docs/loki/latest/api/#post-lokiapiv1push)
* Works with **Android** and **Java SE**
* Thread safe

## API description

HTTP sender requires http URL, optionally basic authentication credentials may be set.

Below sample use case:

```java
import pl.mjaron.tinyloki.*

public class Sample {
    public static void main(String[] args) {
        LogController logController = new LogController(new JsonLogCollector(),
                new LogSender(LogSenderSettings.create()
                        .setUrl("http://localhost/loki/api/v1/push")
                        .setUser("user")
                        .setPassword("pass")),
                new VerboseLogMonitor()).start();
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
## Integration

1. Download lastest release jar from project releases to e.g. `your_project_root/libs` dir.
2. Add this jar to project dependencies in build.gradle  
```gradle
dependencies {
    implementation files(project.rootDir.absolutePath + '/libs/mjaron-tinyloki-java-0.1.1.jar')
}
```
3. Use library in your project.
```java
import pl.mjaron.tinyloki.*
```
