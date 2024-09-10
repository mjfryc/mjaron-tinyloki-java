package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonLogCollectorTest {

    @Test
    void basic() {
        JsonLogCollector collector = new JsonLogCollector();
        JsonLogStream stream = (JsonLogStream) collector.createStream(new Labels().l("abc", "def"));
        stream.log(123, "abc");
        final byte[] collected = collector.collect();
        stream.log(123, "abc");
        final String collectedString = collector.collectAsString();
        collector.onStreamReleased(stream);
        assertEquals("application/json", collector.contentType());
    }
}