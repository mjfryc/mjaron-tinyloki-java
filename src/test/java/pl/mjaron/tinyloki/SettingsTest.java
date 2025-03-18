package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

class SettingsTest {

    @Test
    void normalizeUrl() {
        final String expected = "http://localhost:3100/loki/api/v1/push";
        assertEquals(expected, Settings.normalizeUrl("http://localhost:3100/loki/api/v1/push/"));
        assertEquals(expected, Settings.normalizeUrl("http://localhost:3100/loki/api/v1/push"));
        assertEquals(expected, Settings.normalizeUrl("http://localhost:3100/"));
        assertEquals(expected, Settings.normalizeUrl("http://localhost:3100"));
    }

    @Test
    void basic() {
        ILogCollector collector = new JsonLogCollector();
        ILogEncoder logEncoder = new ILogEncoder() {
            @Override
            public String contentEncoding() {
                return "NO_ENCODING";
            }

            @Override
            public byte[] encode(byte[] what) throws IOException {
                return what;
            }
        };
        Settings settings = TinyLoki.withUrl("http://example.com").withBasicAuth("aaa", "bbb").withConnectTimeout(123).withLogCollector(collector).withLogEncoder(logEncoder);
        assertEquals("aaa", settings.getLogSenderSettings().getUser());
        assertEquals("bbb", settings.getLogSenderSettings().getPassword());
        assertEquals(123, settings.getLogSenderSettings().getConnectTimeout());
        assertSame(collector, settings.getLogCollector());
        assertSame(logEncoder, settings.getLogEncoder());
        settings.withGzipLogEncoder();
        assertEquals(GzipLogEncoder.class, settings.getLogEncoder().getClass());

        ILogMonitor logMonitor = new SilentLogMonitor();
        settings.withLogMonitor(logMonitor);
        assertSame(logMonitor, settings.getLogMonitor());

        settings.withErrorLogMonitor();
        assertEquals(ErrorLogMonitor.class, settings.getLogMonitor().getClass());

        settings.withVerboseLogMonitor();
        assertEquals(VerboseLogMonitor.class, settings.getLogMonitor().getClass());

        ILogSender logSender = new MemoryLogSender();
        settings.withLogSender(logSender);
        assertSame(logSender, settings.getLogSender());
    }

    @Test
    void fromExactUrl() {
        Settings settings = Settings.fromExactUrl("abc");
        assertEquals("abc", settings.getLogSenderSettings().getUrl());
    }
}