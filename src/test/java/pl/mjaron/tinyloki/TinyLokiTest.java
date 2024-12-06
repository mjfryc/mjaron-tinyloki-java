package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class TinyLokiTest {

    @Test
    void dummySendLegacyTest() {
        LogController logController = TinyLoki.withUrl("http://localhost/loki/api/v1/push")
                .withLogSender(new DummyLogSender(1000))
                .withLogMonitor(new VerboseLogMonitor())
                .withLabelLength(1, 1)
                .start();
        ILogStream abcStream = logController.createStream(TinyLoki.info().l("abc", "bcd"));
        abcStream.log(1, "Hello world.");
        logController.softStop().hardStop();
    }

    @Test
    void dummySendTest() {
        LogController logController = TinyLoki.withUrl("http://localhost/loki/api/v1/push")
                .withLogSender(new DummyLogSender(1000))
                .withLogMonitor(new VerboseLogMonitor())
                .withLabelLength(1, 1)
                .start();
        ILogStream abcStream = logController.createStream(TinyLoki.info().l("abc", "bcd"));
        abcStream.log(1, "Hello world.");
        logController.sync();
        logController.stop();
        System.out.println("Done.");
    }

    @Test
    @Disabled
    void tinyLokiTest() {
        LogController logController = TinyLoki.withUrl("http://localhost:3100/loki/api/v1/push").withBasicAuth("user", "pass").withConnectTimeout(5000).start();
        ILogStream stream = logController.stream().info().l("host", "ZEUS").build();
        //Or: ILogStream stream = logController.createStream(TinyLoki.info().l("host", "ZEUS"));
        stream.log("Hello world.");
        // ... new streams and other logs here.
        logController.softStop().hardStop();
    }
}
