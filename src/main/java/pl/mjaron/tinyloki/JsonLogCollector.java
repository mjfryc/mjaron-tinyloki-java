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
    private int logEntriesCount = 0;
    private ILogListener logListener = null;
    private IBuffering bufferingManager = null;
    private LabelSettings structuredMetadataLabelSettings = null;

    @Override
    public void configureLogListener(final ILogListener logListener) {
        this.logListener = logListener;
    }

    @Override
    public void configureBufferingManager(final IBuffering bufferingManager) {
        this.bufferingManager = bufferingManager;
    }

    @Override
    public void configureStructuredMetadata(LabelSettings structuredMetadataLabelSettings) {
        this.structuredMetadataLabelSettings = structuredMetadataLabelSettings;
    }

    /**
     * Provides label settings for structured metadata.
     *
     * @return Label settings for structured metadata. It may be <code>null</code>.
     * @since 1.1.0
     */
    public LabelSettings getStructuredMetadataLabelSettings() {
        return structuredMetadataLabelSettings;
    }

    /**
     * Creates new instance of stream which will notify this collector about new logs.
     * This collector will syncAnd logs from the stream.
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
     * Create complete stream chunk in JSON string, stored as UTF-8 bytes.
     *
     * @return JSON UTF-8 bytes containing stream chunk.
     */
    @Override
    public byte[] collect() {
        final String collectedAsString = collectAsString();
        if (collectedAsString == null) {
            return null;
        }
        return collectedAsString.getBytes(StandardCharsets.UTF_8);
    }

    synchronized public byte[][] collectAll() {
        return bufferingManager.collectAll();
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

    synchronized void logImplementation(JsonLogStream stream, final long timestampMs, final String line, final Labels structuredMetadata) {
        final int logCandidateSize = 25 + line.length();
        if (!bufferingManager.beforeLog(logCandidateSize)) {
            return;
        }

        final int acceptedLogSize = stream.logUnsafe(timestampMs, line, structuredMetadata);

        bufferingManager.logAccepted(acceptedLogSize);
        ++logEntriesCount;
        logListener.onLog(logEntriesCount);
    }
}
