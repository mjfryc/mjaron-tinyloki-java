# mjaron-tinyloki-java

[![Java CI with Gradle](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle.yml/badge.svg)](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle.yml)
[![Gradle Package](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle-publish.yml)

Tiny implementation of Loki client written in pure Java 1.8 without any dependencies.

* Implements JSON variant of [Loki API](https://grafana.com/docs/loki/latest/api/#post-lokiapiv1push)
* Works with **Android** and **Java SE**
* Thread safe

## API description

HTTP sender requires http URL, optionally basic authentication credentials may be set.

Below sample use case:

```java
import pl.mjaron.tinyloki.*;

public class Sample {
    public static void main(String[] args) {
        LogController logController = TinyLoki.createAndStart("http://localhost/loki/api/v1/push", "user", "pass");
        ILogStream stream = logController.createStream(TinyLoki.l(Labels.LEVEL, Labels.INFO).l("host", "ZEUS"));
        stream.log("Hello world.");
        // ... new streams and other logs here.
        logController.softStop().waitForStop();
    }
}
```
## Integration

1. Download lastest release jar from project releases to e.g. `your_project_root/libs` dir.
2. Add this jar to project dependencies in build.gradle  
```gradle
dependencies {
    implementation files(project.rootDir.absolutePath + '/libs/mjaron-tinyloki-java-0.1.3.jar')
}
```
3. Use library in your project.
```java
import pl.mjaron.tinyloki.*;
```
