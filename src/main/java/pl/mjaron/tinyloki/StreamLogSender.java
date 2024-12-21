package pl.mjaron.tinyloki;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Allows writing the logs directly to the given stream.
 * It doesn't open or close the stream.
 *
 * @since 0.4.0
 */
public class StreamLogSender implements ILogSender {

    private final OutputStream outputStream;
    ILogMonitor logMonitor = null;

    public StreamLogSender(final OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    @Override
    public void configure(LogSenderSettings logSenderSettings, ILogMonitor logMonitor) {
        this.logMonitor = logMonitor;
    }

    @Override
    public void send(byte[] message) throws InterruptedException, IOException {
        logMonitor.send(message);
        try {
            outputStream.write(message);
            logMonitor.sendOk(200);
        } catch (final IOException e) {
            logMonitor.sendErr(-1, e.getMessage());
            throw e;
        }
    }
}
