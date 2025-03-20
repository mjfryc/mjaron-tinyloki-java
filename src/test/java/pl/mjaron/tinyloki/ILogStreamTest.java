package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ILogStreamTest {

    @Test
    void log() {
        final TestLogStream stream = new TestLogStream();
        stream.log("sample");
        assertTrue(Math.abs(System.currentTimeMillis() - stream.timestamp) < 1000);
    }

    private static class TestLogStream implements ILogStream {
        public long timestamp = 0;

        @Override
        public void log(long timestampMs, String line, Labels structuredMetadata) {
            timestamp = timestampMs;
        }

        @Override
        public void release() {
        }
    }
}