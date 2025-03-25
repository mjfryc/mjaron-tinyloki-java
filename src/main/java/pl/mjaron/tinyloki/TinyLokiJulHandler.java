package pl.mjaron.tinyloki;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * The log appender which writes logs from the {@link java.util.logging.Logger java.util.logging} library.
 * It may be initialized with one of the {@link #install(Settings)} static methods.
 *
 * @since 1.1.6
 */
public class TinyLokiJulHandler extends java.util.logging.Handler {

    /**
     * Attaches the logger to root logger.
     *
     * @param settings The {@link TinyLoki} library {@link Settings}.
     * @return Created {@link TinyLoki} instance.
     */
    public static TinyLoki install(Settings settings) {
        java.util.logging.Logger rootLogger = java.util.logging.LogManager.getLogManager().getLogger("");
        TinyLokiJulHandler handler = new TinyLokiJulHandler(settings);
        rootLogger.addHandler(handler);
        return handler.getLoki();
    }

    public static void install(String url) {
        install(TinyLoki.withUrl(url));
    }

    public static void install(String url, String user, String pass) {
        install(TinyLoki.withUrl(url).withBasicAuth(user, pass));
    }

    private static class StreamKey {
        final String tag;
        final Level level;
        final int hash;

        private StreamKey(String tag, Level level) {
            this.tag = tag;
            this.level = level;
            this.hash = Objects.hash(tag, level);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            StreamKey that = (StreamKey) o;
            return tag.equals(that.tag) && level == that.level;
        }

        @Override
        public int hashCode() {
            return this.hash;
        }
    }

    private static String toTinyLokiLogLevel(final Level level) {
        final int value = level.intValue();
        if (value >= 1000) {        // SEVERE
            return Labels.FATAL;
        } else if (value >= 900) {  // WARNING
            return Labels.WARN;
        } else if (value >= 800) {  // INFO
            return Labels.INFO;
        } else if (value >= 700) {  // CONFIG
            return Labels.DEBUG;
        } else {
            return Labels.VERBOSE;
        }
    }

    private final TinyLoki loki;
    private Map<StreamKey, ILogStream> streams = new HashMap<>();

    public TinyLokiJulHandler(TinyLoki loki) {
        this.loki = loki;
    }

    public TinyLokiJulHandler(Settings settings) {
        loki = TinyLoki.open(settings);
    }

    public TinyLoki getLoki() {
        return loki;
    }

    @Override
    synchronized public void publish(LogRecord logRecord) {
        StreamKey key = new StreamKey(logRecord.getLoggerName(), logRecord.getLevel());
        ILogStream stream = streams.get(key);
        if (stream == null) {
            Labels streamLabels = Labels.of().l("tag", logRecord.getLoggerName()).l(Labels.LEVEL, toTinyLokiLogLevel(logRecord.getLevel()));
            stream = loki.openStream(streamLabels);
            streams.put(key, stream);
        }
        stream.log(logRecord.getMessage());
    }

    @Override
    public void flush() {
        try {
            loki.sync();
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() throws SecurityException {
        try {
            loki.closeSync();
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
