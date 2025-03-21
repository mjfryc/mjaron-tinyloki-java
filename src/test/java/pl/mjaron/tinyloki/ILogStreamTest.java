package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ILogStreamTest {

    @Test
    void log() {
        final TestLogStream stream = new TestLogStream();
        stream.log("sample");
        //assertTrue(Math.abs(Utils.Nanoseconds.currentTime() - stream.timestamp) < Utils.Nanoseconds.fromSeconds(1));
        assertEquals(ILogStream.TIMESTAMP_NONE, stream.timestamp);
    }

    private static class TestLogStream implements ILogStream {
        public long timestamp = 0;

        @Override
        public void log(long timestampNs, String line, Labels structuredMetadata) {
            timestamp = timestampNs;
        }

        @Override
        public void release() {
        }
    }
}