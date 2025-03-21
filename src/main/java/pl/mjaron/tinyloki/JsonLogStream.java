package pl.mjaron.tinyloki;

import java.util.Map;

/**
 * Writes logs to JSON-formatted string.
 */
public class JsonLogStream implements ILogStream {

    private final static String SEQUENCE_OPENING_STREAM = "{\"stream\":{";
    private final static String SEQUENCE_CLOSING_STREAM = "]}";

    private final JsonLogCollector collector;
    private final StringBuilder b = new StringBuilder(SEQUENCE_OPENING_STREAM);
    private final String initialSequenceWithHeaders;

    private int cachedLogsCount = 0; // Must be used in synchronized methods only.

    /**
     * Constructor of stream. It should be created by {@link JsonLogCollector}.
     *
     * @param collector {@link JsonLogCollector} instance which manages this stream.
     * @param labels    Static labels related to this stream.
     *                  There should not be two streams with the same set of static labels.
     */
    public JsonLogStream(final JsonLogCollector collector, final Labels labels) {
        this.collector = collector;
        boolean isFirst = true;
        for (Map.Entry<String, String> entry : labels.getMap().entrySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                b.append(',');
            }
            b.append('"');
            b.append(entry.getKey());
            b.append("\":\"");
            Utils.escapeJsonString(b, entry.getValue());
            b.append('"');
        }
        b.append("},\"values\":[");
        initialSequenceWithHeaders = b.toString();
    }

    int logUnsafe(final long timestamp, final String line, final Labels structuredMetadata) {
        final int beforeSize = b.length();
        if (cachedLogsCount != 0) {
            b.append(',');
        }
        ++cachedLogsCount;
        b.append("[\"");
        b.append(timestamp);
        b.append("\",\"");
        Utils.escapeJsonString(b, line);
        b.append('"');

        if (structuredMetadata != null && collector.getStructuredMetadataLabelSettings() != null) {
            final Labels prettified = Labels.prettify(structuredMetadata, collector.getStructuredMetadataLabelSettings());
            b.append(",{");
            boolean isFirst = true;
            for (final Map.Entry<String, String> entry : prettified.getMap().entrySet()) {
                if (isFirst) {
                    isFirst = false;
                } else {
                    b.append(',');
                }
                b.append('"');
                b.append(entry.getKey());
                b.append("\":\"");
                Utils.escapeJsonString(b, entry.getValue());
                b.append('"');
            }
            b.append("}");
        }

        b.append("]");
        return b.length() - beforeSize;
    }

    @Override
    public void log(final long timestampNs, final String line, final Labels structuredMetadata) {
        collector.logImplementation(this, timestampNs, line, structuredMetadata);
    }

    @Override
    public void release() {
        collector.onStreamReleased(this);
    }

    /**
     * Appends JSON tags which closes streams array and JSON root object.
     */
    public void closeStreamsEntryTag() {
        b.append(SEQUENCE_CLOSING_STREAM);
    }

    /**
     * Provides access to internal string builder for custom purposes.
     * This method may be changed on any implementation changes.
     *
     * @return internal StringBuilder instance.
     */
    @SuppressWarnings("unused")
    public StringBuilder getStringBuilder() {
        return b;
    }

    /**
     * Drop old data and next prepare StringBuilder to start new chunk of stream.
     */
    public void clear() {
        b.setLength(0);
        b.append(initialSequenceWithHeaders);
        cachedLogsCount = 0;
    }

    /**
     * Provide stream data and clear old logs.
     * <p>
     * Not thread safe.
     * <p>
     * For internal use only.
     * <p>
     * Called internally by {@link JsonLogCollector} in thread managed by {@link IExecutor}.
     *
     * @return JSON String containing single stream logs from last syncAnd operation or from beginning of time.
     * Null if this stream is empty.
     */
    public String flush() {
        if (cachedLogsCount == 0) { // Do not syncAnd if there is no values inside a stream.
            return null;
        }

        closeStreamsEntryTag();
        final String result = b.toString();
        this.clear();
        return result;
    }
}
