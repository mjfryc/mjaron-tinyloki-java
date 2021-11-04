package pl.mjaron.tinyloki;

import java.util.Map;

/**
 * Writes logs to JSON-formatted string.
 */
public class JsonLogStream implements ILogStream {
    private final JsonLogCollector collector;
    private final StringBuilder b = new StringBuilder("{\"stream\":{");
    private final String initialSequenceWithHeaders;
    private int cachedLogsCount = 0; // Must be used in synchronized methods only.

    /**
     * Constructor of stream. It should be created by {@link JsonLogCollector}.
     *
     * @param collector {@link JsonLogCollector} instance which manages this stream.
     * @param labels    Static labels related to this stream.
     *                  There should not be two streams with the same set of static labels.
     */
    public JsonLogStream(JsonLogCollector collector, final Map<String, String> labels) {
        this.collector = collector;
        boolean isFirst = true;
        for (Map.Entry<String, String> entry : labels.entrySet()) {
            if (isFirst) {
                isFirst = false;
            } else {
                b.append(',');
            }
            b.append('"');
            b.append(entry.getKey());
            b.append('"');
            b.append(':');
            b.append('"');
            Utils.escapeJsonString(b, entry.getValue());
            b.append('"');
        }
        b.append("},\"values\":[");
        initialSequenceWithHeaders = b.toString();
    }

    @Override
    public void log(long timestampMs, String line) {
        synchronized (this) {
            if (cachedLogsCount != 0) {
                b.append(',');
            }
            ++cachedLogsCount;
            b.append("[\"");
            b.append(timestampMs);
            b.append("000000\",\"");
            Utils.escapeJsonString(b, line);
            b.append("\"]");
        }
        collector.logOccurred();
    }

    @Override
    public void release() {
        collector.onStreamReleased(this);
    }

    /**
     * Appends JSON tags which closes streams array and JSON root object.
     */
    public void closeStreamsEntryTag() {
        b.append("]}");
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
     *
     * @return JSON-formatted String containing single stream logs from last flush operation or from beginning of time.
     * Null if this stream is empty.
     */
    synchronized public String flush() {
        if (cachedLogsCount == 0) { // Do not flush if there is no values inside a stream.
            return null;
        }

        closeStreamsEntryTag();
        final String result = b.toString();
        this.clear();
        return result;
    }
}
