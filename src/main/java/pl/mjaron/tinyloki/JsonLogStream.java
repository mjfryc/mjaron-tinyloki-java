package pl.mjaron.tinyloki;

import java.util.Map;

public class JsonLogStream implements ILogStream {
    JsonLogCollector collector;
    private StringBuilder b = new StringBuilder("{\"stream\":{");
    private String initialSequenceWithHeaders = null;
    private boolean firstValue = true;

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
            b.append(entry.getValue());
            b.append('"');
        }
        b.append("},\"values\":[");
        initialSequenceWithHeaders = b.toString();
    }

    @Override
    synchronized public void log(long timestampMs, String line) {
        if (firstValue) {
            firstValue = false;
        }
        else {
            b.append(',');
        }
        b.append("[\"");
        b.append(timestampMs);
        b.append("000000\",\"");
        Utils.escapeJsonString(b, line);
        b.append("\"]");
        collector.logOccurred();
    }

    @Override
    public void release() {
        collector.onStreamReleased(this);
    }

    public void closeStreamsEntryTag() {
        b.append("]}");
    }

    public StringBuilder getStringBuilder() {
        return b;
    }

    public void clear() {
        b.setLength(0);
        b.append(initialSequenceWithHeaders);
        firstValue = true;
    }

    synchronized public String flush() {
        closeStreamsEntryTag();
        final String result = b.toString();
        this.clear();
        return result;
    }
}
