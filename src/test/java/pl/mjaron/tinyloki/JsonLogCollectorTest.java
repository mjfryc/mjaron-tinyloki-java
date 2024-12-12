package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonLogCollectorTest {

    @Test
    void basic() {
        JsonLogCollector collector = new JsonLogCollector();
        collector.setLogListener(ILogListener.dummy());
        JsonLogStream stream = (JsonLogStream) collector.createStream(new Labels().l("abc", "def"));
        assertNotNull(stream.getStringBuilder());
        stream.log(123, "abc");
        final byte[] collected = collector.collect();
        stream.log(123, "abc");
        final String collectedString = collector.collectAsString();
        stream.release();
        assertEquals("application/json", collector.contentType());
    }

    @Test
    void emptyStreams() {
        JsonLogCollector collector = new JsonLogCollector();
        collector.setLogListener(ILogListener.dummy());
        ILogStream stream0 = collector.createStream(new Labels().l("a", "Alpha"));
        ILogStream stream1 = collector.createStream(new Labels().l("b", "Beta"));
        assertNull(collector.collect());
        assertNull(collector.collectAsString());
    }

    @Test
    void twoStreams() {
        JsonLogCollector collector = new JsonLogCollector();
        collector.setLogListener(ILogListener.dummy());
        ILogStream stream0 = collector.createStream(new Labels().l("a", "Alpha"));
        ILogStream stream1 = collector.createStream(new Labels().l("b", "Beta"));
        stream0.log("a_line");
        stream1.log("b_line0");
        stream1.log("b_line1");
        final String collected = collector.collectAsString();
        assertNotNull(collected);
        System.out.println("Collected:\n" + collected);
        assertNull(collector.collectAsString());
    }
}
