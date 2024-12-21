package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;


class StreamLogSenderTest {

    private final OutputStream badStream = new OutputStream() {
        @Override
        public void write(int b) throws IOException {
            throw new IOException("I am a bad stream!");
        }
    };

    @Test
    void sendException() {
        StreamLogSender streamLogSender = new StreamLogSender(badStream);
        streamLogSender.configure(null, new VerboseLogMonitor());
        assertThrows(IOException.class, () -> streamLogSender.send("Hello".getBytes(StandardCharsets.UTF_8)));
    }

    @Test
    void sendOk() {

        StreamLogSender streamLogSender = new StreamLogSender(new ByteArrayOutputStream());
        streamLogSender.configure(null, new VerboseLogMonitor());
        assertDoesNotThrow(() -> streamLogSender.send("Hello".getBytes(StandardCharsets.UTF_8)));
    }
}
