package pl.mjaron.tinyloki;

import java.util.Arrays;
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
     * Provides the JUL logger with an empty name.
     *
     * @return Root JUL logger.
     * @since 1.1.6
     */
    public static java.util.logging.Logger getJulRootLogger() {
        return java.util.logging.LogManager.getLogManager().getLogger("");
    }

    /**
     * Attaches the logger to root logger.
     *
     * @param loki The {@link TinyLoki} instance which is already {@link TinyLoki#open(Settings) open}.
     * @return The same {@link TinyLoki} object provided as argument.
     * @since 1.1.7
     */
    public static TinyLoki install(final TinyLoki loki) {
        final java.util.logging.Logger rootLogger = getJulRootLogger();
        final TinyLokiJulHandler handler = new TinyLokiJulHandler(loki);
        rootLogger.addHandler(handler);
        return handler.getLoki();
    }

    /**
     * Attaches the logger to root logger.
     *
     * @param settings The {@link TinyLoki} library {@link Settings}.
     * @return Created {@link TinyLoki} instance.
     * @since 1.1.7
     */
    public static TinyLoki install(final Settings settings) {
        return install(settings.open());
    }

    /**
     * Install the new instance of {@link TinyLoki} with default settings and given URL.
     *
     * @param url Destination where to send the logs.
     * @since 1.1.6
     */
    public static void install(final String url) {
        install(TinyLoki.withUrl(url));
    }

    /**
     * Install the new instance of {@link TinyLoki} with default settings and given URL and basic auth credentials.
     *
     * @param url  Destination where to send the logs.
     * @param user Basic auth user.
     * @param pass Basic auth password.
     * @since 1.1.6
     */
    public static void install(final String url, final String user, final String pass) {
        install(TinyLoki.withUrl(url).withBasicAuth(user, pass));
    }

    /**
     * Sets the log level of whole JUL framework:
     * <ul>
     *     <li>The root logger.</li>
     *     <li>All log handlers of root logger.</li>
     * </ul>
     *
     * @param newLevel The new level.
     * @since 1.1.7
     */
    public static void setJulLogLevel(final java.util.logging.Level newLevel) {
        java.util.logging.Logger rootLogger = getJulRootLogger();
        rootLogger.setLevel(newLevel);
        Arrays.stream(rootLogger.getHandlers()).forEach(h -> h.setLevel(newLevel));
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
        public boolean equals(final Object o) {
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

    public static String toTinyLokiLogLevel(final Level level) {
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
    private final Map<StreamKey, ILogStream> streams = new HashMap<>();

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
    synchronized public void publish(final LogRecord logRecord) {
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
        } catch (final InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void close() throws SecurityException {
        try {
            loki.closeSync();
        } catch (final InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }
}
