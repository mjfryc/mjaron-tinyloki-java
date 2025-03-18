package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class TinyLokiTest {

    @Test
    void dummySendLegacyTest() {
        LogController logController = TinyLoki.withUrl("http://localhost/loki/api/v1/push").withLogSender(new DummyLogSender(1000)).withLogMonitor(new VerboseLogMonitor()).withLabelLength(1, 1).start();
        ILogStream abcStream = logController.createStream(TinyLoki.info().l("abc", "bcd"));
        abcStream.log(1, "Hello world.");
        logController.softStop().hardStop();
    }

    @Test
    void dummySendTest() throws InterruptedException {
        LogController logController = TinyLoki.withUrl("http://localhost/loki/api/v1/push").withLogSender(new DummyLogSender(LogController.DEFAULT_SYNC_TIMEOUT - 100)).withLogMonitor(new VerboseLogMonitor()).withLabelLength(1, 1).withExecutor(new ThreadExecutor(10 * 1000)).start();
        ILogStream abcStream = logController.stream().info().l("abc", "bcd").build();
        abcStream.log(1, "Hello world.");
        long t0 = System.currentTimeMillis();
        final boolean result = logController.closeSync(LogController.DEFAULT_SYNC_TIMEOUT, 3000);
        System.out.println("closeSync time: " + (System.currentTimeMillis() - t0));
        assertTrue(result);
        System.out.println("Done.");
    }
}
