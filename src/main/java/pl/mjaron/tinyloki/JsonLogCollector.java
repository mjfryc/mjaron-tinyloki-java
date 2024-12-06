package pl.mjaron.tinyloki;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Collects logs in a JSON format consistent with
 * <a href="https://grafana.com/docs/loki/latest/api/#post-lokiapiv1push">Loki Push API</a>.
 */
public class JsonLogCollector implements ILogCollector {

    public static final String CONTENT_TYPE = "application/json";

    private final List<JsonLogStream> streams = new ArrayList<>();
    private final Object logEntriesLock = new Object();
    private int logEntriesCount = 0;
    private ILogListener logObserver = null;

    @Override
    public void setLogListener(ILogListener logListener) {
        this.logObserver = logListener;
    }

    /**
     * Creates new instance of stream which will notify this collector about new logs.
     * This collector will flush logs from the stream.
     *
     * @param labels Unique set of labels.
     * @return New instance of a stream.
     */
    @Override
    synchronized public ILogStream createStream(final Labels labels) {
        JsonLogStream stream = new JsonLogStream(this, labels);
        streams.add(stream);
        return stream;
    }

    /**
     * Given stream will not be flushed by this log collector anymore, so given stream will accumulate
     * all next logs causing memory leaks (if it will not be garbage collected).
     * Called by {@link JsonLogStream#release()}, so there is no need to call it directly.
     *
     * @param stream Stream to release.
     */
    synchronized public void onStreamReleased(ILogStream stream) {
        streams.remove((JsonLogStream) stream);
    }

    /**
     * Create complete stream chunk in JSON string, stored as UTF-8 bytes.
     *
     * @return JSON bytes containing stream chunk.
     */
    @Override
    public byte[] collect() {
        final String collectedAsString = collectAsString();
        if (collectedAsString == null) {
            return null;
        }
        return collectedAsString.getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Create complete stream chunk in JSON string.
     *
     * @return JSON string containing stream chunk.
     */
    synchronized public String collectAsString() {
        final StringBuilder b = new StringBuilder("{\"streams\":[");
        boolean isFirst = true;
        boolean anyStreamNotEmpty = false;
        for (final JsonLogStream stream : streams) {
            final String streamData = stream.flush();
            if (streamData != null) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    b.append(',');
                }
                b.append(streamData);
                anyStreamNotEmpty = true;
            }
        }

        if (!anyStreamNotEmpty) {
            return null;
        }

        b.append("]}");
        return b.toString();
    }

    /**
     * Used in HTTP Content-Type header. Grafana Loki will interpret content as JSON.
     *
     * @return String complaint with HTTP Content-Type header, telling that content is a JSON data.
     */
    @Override
    public String contentType() {
        return CONTENT_TYPE;
    }

    /**
     * Notify log collector that any log has occurred.
     * Thread safe.
     */
    void logOccurred() {
        synchronized (logEntriesLock) {
            ++logEntriesCount;
            logEntriesLock.notify();
            logObserver.onLog(logEntriesCount);
        }
    }

    /**
     * Blocking function. Waits until at least one log from any stream occurs.
     *
     * @param timeout Time in milliseconds.
     * @return Collected logs count.
     * @throws InterruptedException When this thread is interrupted during waiting.
     * @deprecated
     */
    @Override
    @Deprecated
    public int waitForLogs(final long timeout) throws InterruptedException {
        synchronized (logEntriesLock) {
            if (logEntriesCount == 0) {
                logEntriesLock.wait(timeout);
            }
            final int logEntriesCountCopy = logEntriesCount;
            logEntriesCount = 0;
            return logEntriesCountCopy;
        }
    }
}
