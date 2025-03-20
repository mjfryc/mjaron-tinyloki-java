# mjaron-tinyloki-java

> [!IMPORTANT]  
> Please use version [v0.3.11](https://github.com/mjfryc/mjaron-tinyloki-java/tree/v0.3.11)  
> The current master version `v1.x.x` still in preparation.

Building  
[![Java CI with Gradle](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle.yml/badge.svg)](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle.yml)
[![Gradle Package](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle-publish.yml/badge.svg)](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/gradle-publish.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.mjfryc/mjaron-tinyloki-java?color=dark-green&style=flat)](https://search.maven.org/artifact/io.github.mjfryc/mjaron-tinyloki-java/)

Code quality  
[![CodeQL](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/codeql.yml/badge.svg)](https://github.com/mjfryc/mjaron-tinyloki-java/actions/workflows/codeql.yml)
![snyk.io](https://snyk.io/test/github/mjfryc/mjaron-tinyloki-java/badge.svg)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=mjfryc_mjaron-tinyloki-java&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=mjfryc_mjaron-tinyloki-java)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=mjfryc_mjaron-tinyloki-java&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=mjfryc_mjaron-tinyloki-java)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=mjfryc_mjaron-tinyloki-java&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=mjfryc_mjaron-tinyloki-java)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=mjfryc_mjaron-tinyloki-java&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=mjfryc_mjaron-tinyloki-java)

Unit tests  
![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)

Tiny [Grafana Loki](https://grafana.com/oss/loki/) client (log sender) written in pure Java 1.8 without any external
dependencies. One of Grafana Loki third-party clients mentioned in [documentation](https://grafana.com/docs/loki/v3.4.x/send-data/).

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
        TinyLoki loki = TinyLoki.withUrl("http://localhost:3100").withBasicAuth("user", "pass").open();
        ILogStream logStream = loki.stream().info().l("topic", "shortExample").open();
        logStream.log("Hello world!");
        logStream.log("Hello world!", Labels.of("structured_metadata", "My log metadata value."));
        loki.closeSync();
    }
}
```

Verbose example:

```java
import pl.mjaron.tinyloki.*;

public class Sample {
    public static void main(String[] args) {

        // Initialize the log controller instance with URL.
        // The endpoint loki/api/v1/push will be added by default if missing.
        // Usually creating more than one TinyLoki instance doesn't make sense.
        // TinyLoki (its default IExecutor implementation) owns separate thread which sends logs periodically.
        // It may be called inside try-with-resources block, but the default close() method doesn't synchronize the logs,
        // but just interrupts the background worker thread.
        try (TinyLoki loki = TinyLoki.withUrl("http://localhost:3100/loki/api/v1/push")
                // Print all diagnostic information coming from the TinyLoki library. For diagnostic purposes only.
                .withVerboseLogMonitor()

                // Set the custom log processing interval time.
                // So the executor will try to send the next logs 10 seconds after the previous logs sending operation.
                .withThreadExecutor(10 * 1000)

                // Set custom time of HTTP connection establishing timeout.
                .withConnectTimeout(10 * 1000)

                // Encode the logs to limit the size of data sent.
                .withGzipLogEncoder()

                // The BasicBuffering is set by default, but here the (not encoded) message size limit may be customized.
                .withBasicBuffering(3 * 1024 * 1024, 10)

                // Initialize the library with above settings.
                // The ThreadExecutor will create a new thread and start waiting for the logs to be sent.
                .start()) {

            // Some logs here...
            ILogStream whiteStream = loki.stream().l("color", "white").build();
            whiteStream.log("Hello white world.");

            // Blocking method, tries to send the logs ASAP and wait for sending completion.
            // This method returns false when timeout occurs, but true when sending has completed with success or failure.
            boolean allHttpSendingOperationsFinished = loki.sync();
            System.out.println("Are all logs processed: " + allHttpSendingOperationsFinished);

            ILogStream redStream = loki.stream().l("color", "red").build();
            redStream.log("Hello white world.");

            // Blocking method, tries to synchronize the logs than interrupt and join the execution thread.
            // Set the custom timeout time for this operation.
            boolean closedWithSuccess = loki.closeSync(5 * 1000);

            System.out.println("Synced and closed with success: " + closedWithSuccess);
        }
    }
}
```

## Integration

### Maven Central

```gradle
    implementation 'io.github.mjfryc:mjaron-tinyloki-java:1.0.0'
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
    implementation files(project.rootDir.absolutePath + '/libs/mjaron-tinyloki-java-1.0.0.jar')
```

## Features description

### Structured metadata

The [structured metadata](https://grafana.com/docs/loki/v3.4.x/get-started/labels/structured-metadata/) has been enabled
by default in [Grafana Loki 3.0.0](https://grafana.com/docs/loki/v3.4.x/setup/upgrade/#loki-300).
To put structured metadata to the log line, add it as the last argument of logging method.

## API design (outdated)

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
