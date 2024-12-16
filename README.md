# mjaron-tinyloki-java

[![Java CI with Gradle](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle.yml/badge.svg)](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle.yml)
[![Gradle Package](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle-publish.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mjfryc/mjaron-tinyloki-java?color=dark-green&style=flat)](https://search.maven.org/artifact/io.github.mjfryc/mjaron-tinyloki-java/)

[![CodeQL](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/codeql.yml/badge.svg)](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/codeql.yml)
![snyk.io](https://snyk.io/test/github/mjfryc/mjaron-tinyloki-java/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=mjfryc_mjaron-tinyloki-java&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=mjfryc_mjaron-tinyloki-java)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=mjfryc_mjaron-tinyloki-java&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=mjfryc_mjaron-tinyloki-java)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=mjfryc_mjaron-tinyloki-java&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=mjfryc_mjaron-tinyloki-java)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=mjfryc_mjaron-tinyloki-java&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=mjfryc_mjaron-tinyloki-java)

![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

[![Milestone](https://img.shields.io/github/milestones/progress-percent/mjfryc/mjaron-tinyloki-java/1?label=Next%20release:%20milestone-v0.4.0)](https://github.com/mjfryc/mjaron-tinyloki-java/milestone/1)


Tiny [Grafana Loki](https://grafana.com/oss/loki/) client (log sender) written in pure Java 1.8 without any external dependencies.

* Implements JSON variant of [Loki API](https://grafana.com/docs/loki/latest/api/#post-lokiapiv1push)
* Works with **Android** and **Java SE**
* Thread safe

## API description

HTTP sender requires http URL, optionally basic authentication credentials may be set.

Short example:
```java
import pl.mjaron.tinyloki.*;

public class Sample {
    public static void main(String[] args) {

        LogController logController = TinyLoki
                .withUrl("http://localhost/loki/api/v1/push")
                .withBasicAuth("user", "pass")
                .start();

        ILogStream stream = logController.stream() // Before v0.3.2 use createStream()
                .info()
                .l("host", "MyComputerName")
                .l("customLabel", "custom_value")
                .build();

        stream.log("Hello world.");

        logController.softStop().hardStop();
    }
}
```

Verbose example:

```java
import pl.mjaron.tinyloki.*;

public class Sample {
    public static void main(String[] args) {

        // Initialize log controller instance with URL.
        // Usually creating more than one LogController instance doesn't make sense.
        // LogController owns separate thread which sends logs periodically.
        LogController logController = TinyLoki
                .withUrl("http://localhost/loki/api/v1/push")
                .withBasicAuth("user", "pass")
                .start();

        // Create streams. It is thread-safe.
        ILogStream stream = logController.createStream(
                // Define stream labels...
                // Initializing log level to verbose and adding some custom labels.
                TinyLoki.verbose()
                        .l("host", "MyComputerName")        // Custom static label.
                        .l("customLabel", "custom_value")   // Custom static label.
                // Label names should start with letter
                //     and contain letters, digits and '_' only.
                // Bad characters will be replaced by '_'.
                //     If first character is bad, it will be replaced by 'A'.
        );

        // ... new streams and other logs here (thread-safe, non-blocking).
        stream.log("Hello world.");

        // Optionally flush logs before application exit.
        logController
                .softStop()     // Try to send logs last time. Blocking method.
                .hardStop();    // If it doesn't work (soft timeout) - force stop sending thread.
    }
}
```

## Integration

### Maven Central

```gradle
    implementation 'io.github.mjfryc:mjaron-tinyloki-java:0.3.11'
```

 _[Maven Central page](https://search.maven.org/artifact/io.github.mjfryc/mjaron-tinyloki-java/),_
 _[Maven Central repository URL](https://repo1.maven.org/maven2/io/github/mjfryc/mjaron-tinyloki-java/)_

### GitHub Packages

Click the [Packages section](https://github.com/mjfryc?tab=packages&repo_name=mjaron-tinyloki-java) on the right.

### Download directly

1. Click the [Packages section](https://github.com/mjfryc?tab=packages&repo_name=mjaron-tinyloki-java) on the right.
2. Find and download jar package from files list to e.g. `your_project_root/libs` dir.
3. Add this jar to project dependencies in build.gradle, e.g:

```gradle
    implementation files(project.rootDir.absolutePath + '/libs/mjaron-tinyloki-java-0.3.11.jar')
```

## API design

```mermaid
classDiagram
class Labels {
    l(name, value) // Add the label
}

class ILogStream {
    <<Interface>>
    log(content)
    log(timestamp, content)
    release()
}

class ILogCollector {
    <<Interface>>
    createStream(labels)
    collect()
    contentType()
    waitForLogs(timeout)
}

class ILogEncoder {
    <<Interface>>
    contentEncoding(): String
    encode(final byte[] what): byte[]
}

class ILogMonitor {
    <<Interface>>
    onConfigured(contentType, contentEncoding)
    onEncoded(in, out)
    send(message)
    sendOk(status)
    sendErr(status, message)
    onException(exception)
    onWorkerThreadExit(isSoft)
}

class ILogSender {
    <<Interface>>
    configure(logSenderSettings, logMonitor)
    send(bytes)
}

class LogController {
    workerThread: Thread
    stream(): StreamBuilder
    createStream(labels)
    start()
    softStop()
    hardStop()
}

class GzipLogEncoder

class VerboseLogMonitor
class ErrorLogMonitor
class JsonLogStream
class JsonLogCollector

class Settings {
    +start(): LogController
}

class TinyLoki {
    +withUrl(url)$
}

VerboseLogMonitor --|> ILogMonitor: implements
ErrorLogMonitor --|> ILogMonitor: implements

ILogStream <.. ILogCollector: create
ILogCollector --* LogController
Labels <.. ILogCollector : use
ILogSender --* LogController
GzipLogEncoder --|> ILogEncoder: implements
ILogEncoder --* LogController
ILogMonitor --* LogController
JsonLogStream --|>  ILogStream: implements
JsonLogCollector --|> ILogCollector: implements
JsonLogStream <.. JsonLogCollector: create
HttpLogSender --|> ILogSender: implements
DummyLogSender --|> ILogSender: implements
Settings <.. TinyLoki: create
LogController <.. Settings: create
```
