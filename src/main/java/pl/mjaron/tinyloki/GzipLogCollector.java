package pl.mjaron.tinyloki;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * Compresses Json logs internally collected with {@link JsonLogCollector}, so final format is Gzip.
 */
public class GzipLogCollector implements ILogCollector {

    /**
     * Used to collect uncompressed Json logs.
     */
    private final JsonLogCollector jsonLogCollector = new JsonLogCollector();

    /**
     * Creates {@link JsonLogStream}, used to collect uncompressed Json logs.
     *
     * @param labels Unique set of labels.
     * @return new {@link ILogStream} instance.
     */
    @Override
    public ILogStream createStream(Labels labels) {
        return jsonLogCollector.createStream(labels);
    }

    /**
     * Collects all uncompressed Json logs and next compresses it to Gzip.
     *
     * @return Json compressed with Gzip.
     */
    @Override
    public byte[] collect() {
        final byte[] uncompressed = jsonLogCollector.collect();
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(uncompressed.length);
        try (final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bos)) {
            gzipOutputStream.write(uncompressed);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        return bos.toByteArray();
    }

    /**
     * Provides Gzip content type, as descibed in
     * <a href="https://grafana.com/docs/loki/latest/api/#push-log-entries-to-loki">Loki Push API / Push log entries to
     * Loki</a>.
     *
     * @return Gzip content type.
     */
    @Override
    public String contentType() {
        return "gzip";
    }

    /**
     * Delegates waiting for logs to internal Json log collector.
     *
     * @param timeout Time in milliseconds.
     * @return Collected logs count.
     * @throws InterruptedException When this thread is interrupted during waiting.
     * @see JsonLogCollector#waitForLogs(long)
     */
    @Override
    public int waitForLogs(long timeout) throws InterruptedException {
        return jsonLogCollector.waitForLogs(timeout);
    }
}
