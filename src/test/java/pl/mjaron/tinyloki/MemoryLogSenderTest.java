package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class MemoryLogSenderTest {

    @Test
    void basic() throws IOException, InterruptedException {
        final byte[] data = "Hello".getBytes(StandardCharsets.UTF_8);
        MemoryLogSender sender = new MemoryLogSender();
        sender.configure(null, new VerboseLogMonitor());
        sender.send(data);
        assertArrayEquals(data, sender.get());
        assertEquals("Hello", sender.getAsString());
        sender.clear();
        assertEquals(0, sender.get().length);
        assertEquals("", sender.getAsString());
    }
}
