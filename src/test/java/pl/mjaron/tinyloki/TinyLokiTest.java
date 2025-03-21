package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TinyLokiTest {

    @Test
    void dummySendLegacyTest() {
        TinyLoki tinyLoki = TinyLoki.withUrl("http://localhost/loki/api/v1/push").withLogSender(new DummyLogSender(1000)).withLogMonitor(new VerboseLogMonitor()).withLabelLength(1, 1).open();
        ILogStream abcStream = tinyLoki.openStream(Labels.of().info().l("abc", "bcd"));
        abcStream.log(1, "Hello world.");
        tinyLoki.softStop().hardStop();
    }

    @Test
    void dummySendTest() throws InterruptedException {
        TinyLoki tinyLoki = TinyLoki.withUrl("http://localhost/loki/api/v1/push").withLogSender(new DummyLogSender(TinyLoki.DEFAULT_SYNC_TIMEOUT - 100)).withLogMonitor(new VerboseLogMonitor()).withLabelLength(1, 1).withExecutor(new ThreadExecutor(10 * 1000)).open();
        ILogStream abcStream = tinyLoki.stream().info().l("abc", "bcd").open();
        abcStream.log(1, "Hello world.");
        long t0 = System.currentTimeMillis();
        final boolean result = tinyLoki.closeSync(TinyLoki.DEFAULT_SYNC_TIMEOUT, 3000);
        System.out.println("closeSync time: " + (System.currentTimeMillis() - t0));
        assertTrue(result);
        System.out.println("Done.");
    }
}
