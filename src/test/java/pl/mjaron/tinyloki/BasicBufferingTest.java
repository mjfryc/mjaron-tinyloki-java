package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class BasicBufferingTest {

    @Test
    void basic() {

        ILogCollector c = new ILogCollector() {
            @Override
            public void configureLogListener(ILogListener logListener) {

            }

            @Override
            public void configureBufferingManager(IBuffering bufferingManager) {

            }

            @Override
            public ILogStream createStream(Labels labels) {
                return null;
            }

            @Override
            public byte[] collect() {
                return new byte[0];
            }

            @Override
            public byte[][] collectAll() {
                return new byte[][]{"ABC".getBytes(StandardCharsets.UTF_8)};
            }

            @Override
            public String contentType() {
                return "";
            }
        };

        IExecutor executor = new SyncExecutor();

        BasicBuffering b = new BasicBuffering();

        b.configure(c, 2, 3, executor, new VerboseLogMonitor());
        assertTrue(b.beforeLog(1));
        b.logAccepted(1);
        assertEquals(0, b.getBuffers().size());

        assertFalse(b.beforeLog(10));
        assertTrue(b.beforeLog(2));
        b.logAccepted(2);
        assertEquals(1, b.getBuffers().size());

        assertTrue(b.beforeLog(2));
        b.logAccepted(2);

        assertTrue(b.beforeLog(2));
        b.logAccepted(2);
        assertEquals(3, b.getBuffers().size());

        assertTrue(b.beforeLog(2));
        b.logAccepted(2);
        assertEquals(3, b.getBuffers().size());
    }

    @Test
    void nullCollector() {
        ILogCollector c = new ILogCollector() {
            @Override
            public void configureLogListener(ILogListener logListener) {
            }

            @Override
            public void configureBufferingManager(IBuffering bufferingManager) {
            }

            @Override
            public ILogStream createStream(Labels labels) {
                return null;
            }

            @Override
            public byte[] collect() {
                return null;
            }

            @Override
            public byte[][] collectAll() {
                return null;
            }

            @Override
            public String contentType() {
                return "";
            }
        };

        IExecutor executor = new SyncExecutor();

        BasicBuffering b = new BasicBuffering();

        b.configure(c, 2, 3, executor, new VerboseLogMonitor());

        b.beforeLog(2);
        b.logAccepted(2);

        b.beforeLog(2);
        b.logAccepted(2);

        assertTrue(b.getBuffers().isEmpty());
        assertNull(b.collectAll());
    }
}