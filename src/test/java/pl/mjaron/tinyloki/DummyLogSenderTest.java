package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class DummyLogSenderTest {

    @Test
    void basic() {
        assertThrows(RuntimeException.class, () -> new DummyLogSender(-1));
        assertDoesNotThrow(() -> new DummyLogSender(0));
        assertDoesNotThrow(() -> new DummyLogSender(1));
    }

    @Test
    void sendDelay() throws InterruptedException {
        DummyLogSender sender = new DummyLogSender(10);
        ILogMonitor logMonitor = new VerboseLogMonitor();
        logMonitor.onConfigured("DUMMY", "PLAIN");
        sender.configure(new LogSenderSettings(), logMonitor);
        assertTimeout(Duration.ofMillis(100), () -> sender.send("ABC".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void sendNoDelay() throws InterruptedException {
        DummyLogSender sender = new DummyLogSender();
        ILogMonitor logMonitor = new VerboseLogMonitor();
        logMonitor.onConfigured("DUMMY", "PLAIN");
        sender.configure(new LogSenderSettings(), logMonitor);
        assertTimeout(Duration.ofMillis(5), () -> sender.send("ABC".getBytes(StandardCharsets.UTF_8)));
    }
}
