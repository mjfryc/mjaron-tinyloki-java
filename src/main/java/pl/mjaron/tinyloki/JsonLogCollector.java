package pl.mjaron.tinyloki;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonLogCollector implements ILogCollector {

    private final List<JsonLogStream> streams = new ArrayList<>();
    private boolean logOccurred = false;

    @Override
    synchronized public ILogStream createStream(Map<String, String> labels) {
        JsonLogStream stream = new JsonLogStream(this, labels);
        streams.add(stream);
        return stream;
    }

    synchronized public void onStreamReleased(ILogStream stream) {
        streams.remove((JsonLogStream) stream);
    }

    @Override
    public byte[] collect() {
        return collectAsString().getBytes(StandardCharsets.UTF_8);
    }

    synchronized public String collectAsString() {
        final StringBuilder b = new StringBuilder("{\"streams\":[");
        boolean isFirst = true;
        for (final JsonLogStream stream : streams) {
            if (isFirst) {
                isFirst = false;
            } else {
                b.append(',');
            }
            b.append(stream.flush());
        }
        b.append("]}");
        return b.toString();
    }

    @Override
    public String contentType() {
        return "application/json";
    }

    synchronized void logOccurred() {
        logOccurred = true;
        notify();
    }

    @Override
    public synchronized boolean waitForLogs(final long timeout) throws InterruptedException {
        if (!logOccurred) {
            this.wait(timeout);
        }
        if (logOccurred) {
            logOccurred = false;
            return true;
        }
        return false;
    }
}
