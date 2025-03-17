package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class VerboseLogMonitorTest {

    @Test
    void send() {

        final VerboseLogMonitor logMonitor = new VerboseLogMonitor(false);
        logMonitor.onConfigured(JsonLogCollector.CONTENT_TYPE, null);
        assertDoesNotThrow(() -> logMonitor.send("Alicia has a cat".getBytes(StandardCharsets.UTF_8)));
    }
}