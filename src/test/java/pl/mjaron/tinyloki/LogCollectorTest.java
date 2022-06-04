package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.TreeMap;

class LogCollectorTest {

    @Test
    void basicJson() {
        JsonLogCollector collector = new JsonLogCollector();
        Labels labels = new Labels();
        labels.l("level", "INFO");
        labels.l("host", "ZEUS");
        ILogStream stream = collector.createStream(labels);
        stream.log(1635710583043L, "Hello world.");
        final String collected = collector.collectAsString();
        System.out.println("Collected:\n" + collected);
        final String expected = "{\"streams\":[{\"stream\":{\"host\":\"ZEUS\",\"level\":\"INFO\"},\"values\":[[\"1635710583043000000\",\"Hello world.\"]]}]}";
        Assertions.assertEquals(expected, collected);

// Logs may be sent manually, eg:
//        LogSender logSender = new LogSender(LogSenderSettings.create()
//                .setUrl("http://localhost/loki/api/v1/push")
//                .setUser("user")
//                .setPassword("pass")
//                .setContentType(collector.contentType()));
//        logSender.send(collected.getBytes(StandardCharsets.UTF_8));
//        System.out.println("All done.");
    }
}
