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
    
        // Initialize log controller instance with URL.
        // Usually more than one instance in application doesn't make sense.
        // Give Basic Authentication credentials or nulls.
        // LogController owns separate thread which sends logs periodically.
        LogController logController = TinyLoki.createAndStart(
            "https://localhost/loki/api/v1/push", "user", "pass");
        
        // Create streams. It is thread-safe.
        ILogStream stream = logController.createStream(
            // Define stream labels...
            TinyLoki.l(Labels.LEVEL, Labels.INFO)
                    .l("host", "MyComputerName")
                    .l("custom-label", "custom-value")
        );
        
        // ... new streams and other logs here (thread-safe).
        stream.log("Hello world.");
        
        // Optionally flush logs before application exit.
        logController.softStop().waitForStop();
    }
}
```
## Integration

### Maven Central

[Maven Central page](https://search.maven.org/artifact/io.github.mjfryc/mjaron-tinyloki-java/0.1.22/jar)

```gradle
dependencies {
    implementation 'io.github.mjfryc:mjaron-tinyloki-java:0.1.22'
}
```
### GitHub Packages

Click the [Packages section](https://github.com/mjfryc?tab=packages&repo_name=mjaron-tinyloki-java) on the right.

### Download directly
1. Click the [Packages section](https://github.com/mjfryc?tab=packages&repo_name=mjaron-tinyloki-java) on the right.
2. Find and download jar package from files list to e.g. `your_project_root/libs` dir.
3. Add this jar to project dependencies in build.gradle, e.g:
```gradle
dependencies {
    implementation files(project.rootDir.absolutePath + '/libs/mjaron-tinyloki-java-0.1.22.jar')
}
```
