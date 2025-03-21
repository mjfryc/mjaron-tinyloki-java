package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
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

    @Test
    void getLogMonitor() {
        final ILogMonitor logMonitor = new SilentLogMonitor();
        final TinyLoki controller = TinyLoki.withUrl("http://example.com").withLogMonitor(logMonitor).open();
        assertSame(logMonitor, controller.getLogMonitor());

    }

    @Test
    void getLogCollector() {
        final ILogCollector collector = new JsonLogCollector();
        final TinyLoki controller = TinyLoki.withUrl("http://example.com").withLogCollector(collector).open();
        assertSame(collector, controller.getLogCollector());
    }

    @Test
    void getExecutor() {
        final IExecutor executor = new ThreadExecutor();
        final TinyLoki controller = TinyLoki.withUrl("http://example.com").withExecutor(executor).open();
        assertSame(executor, controller.getExecutor());
    }

    @Test
    void createStreamFromMap() throws InterruptedException {
        MemoryLogSender logSender = new MemoryLogSender();
        final TinyLoki controller = TinyLoki.withUrl("http://example.com").withLogSender(logSender).open();
        Map<String, String> map = new HashMap<>();
        map.put("aaa", "bbb");
        ILogStream stream = controller.openStream(map);
        stream.log(0, "line");
        assertTrue(controller.sync());
        assertTrue(controller.stop());
        assertSame(controller, controller.stopAsync());

        final String string = new String(logSender.get(), StandardCharsets.UTF_8);
        System.out.println("Sent: " + string);
        assertTrue(string.contains("aaa"));
        assertTrue(string.contains("bbb"));
        assertTrue(string.contains("line"));
    }
}
