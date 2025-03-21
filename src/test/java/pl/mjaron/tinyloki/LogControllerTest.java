package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LogControllerTest {

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
