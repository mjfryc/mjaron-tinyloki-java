package pl.mjaron.tinyloki;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Writes logs to memory.
 * <p>
 * If memory is not cleared with {@link #clear()},
 * stores all the logs which may cause potentially a memory leak.
 *
 * @since 1.0.0
 */
public class MemoryLogSender implements ILogSender {

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    private final StreamLogSender streamLogSender = new StreamLogSender(outputStream);

    @Override
    public void configure(LogSenderSettings logSenderSettings, ILogMonitor logMonitor) {
        streamLogSender.configure(logSenderSettings, logMonitor);
    }

    @Override
    public void send(byte[] message) throws InterruptedException, IOException {
        streamLogSender.send(message);
    }

    public byte[] get() {
        return outputStream.toByteArray();
    }

    public String getAsString() {
        return new String(get(), StandardCharsets.UTF_8);
    }

    void clear() {
        outputStream.reset();
    }
}
