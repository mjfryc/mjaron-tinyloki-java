package pl.mjaron.tinyloki;

/**
 * A set of streams with different log level label values but common other labels.
 *
 * @since 1.1.1
 */
public class StreamSet {

    private final TinyLoki loki;
    private final Labels labels;

    private ILogStream fatalStream;
    private ILogStream warningStream;
    private ILogStream infoStream;
    private ILogStream debugStream;
    private ILogStream traceStream;
    private ILogStream unknownStream;

    StreamSet(final TinyLoki loki, final Labels labels) {
        this.loki = loki;
        this.labels = labels;
    }

    /**
     * Releases all internal streams.
     *
     * @since 1.1.1
     */
    synchronized void release() {
        if (fatalStream != null) {
            fatalStream.release();
        }
        if (warningStream != null) {
            warningStream.release();
        }
        if (infoStream != null) {
            infoStream.release();
        }
        if (debugStream != null) {
            debugStream.release();
        }
        if (traceStream != null) {
            traceStream.release();
        }
        if (unknownStream != null) {
            unknownStream.release();
        }
    }

    /**
     * Provides a stream with common labels and requested log level.
     *
     * @return Stream with {@link Labels#FATAL} log level.
     * @since 1.1.1
     */
    synchronized ILogStream fatal() {
        if (fatalStream == null) {
            fatalStream = loki.stream().fatal().l(labels).open();
        }
        return fatalStream;
    }

    /**
     * Provides a stream with common labels and requested log level.
     *
     * @return Stream with {@link Labels#WARN} log level.
     * @since 1.1.1
     */
    synchronized ILogStream warning() {
        if (warningStream == null) {
            warningStream = loki.stream().warning().l(labels).open();
        }
        return warningStream;
    }

    /**
     * Provides a stream with common labels and requested log level.
     *
     * @return Stream with {@link Labels#INFO} log level.
     * @since 1.1.1
     */
    synchronized ILogStream info() {
        if (infoStream == null) {
            infoStream = loki.stream().info().l(labels).open();
        }
        return infoStream;
    }

    /**
     * Provides a stream with common labels and requested log level.
     *
     * @return Stream with {@link Labels#DEBUG} log level.
     * @since 1.1.1
     */
    synchronized ILogStream debug() {
        if (debugStream == null) {
            debugStream = loki.stream().debug().l(labels).open();
        }
        return debugStream;
    }

    /**
     * Provides a stream with common labels and requested log level.
     *
     * @return Stream with {@link Labels#VERBOSE} log level.
     * @since 1.1.1
     */
    synchronized ILogStream verbose() {
        if (traceStream == null) {
            traceStream = loki.stream().trace().l(labels).open();
        }
        return traceStream;
    }

    /**
     * Provides a stream with common labels and requested log level.
     *
     * @return Stream with {@link Labels#UNKNOWN} log level.
     * @since 1.1.1
     */
    synchronized ILogStream unknown() {
        if (unknownStream == null) {
            unknownStream = loki.stream().unknown().l(labels).open();
        }
        return unknownStream;
    }

    /**
     * Writes the log with common labels and {@link Labels#FATAL} log level.
     *
     * @param line Log message.
     * @since 1.1.1
     */
    void fatal(final String line) {
        fatal().log(line);
    }

    /**
     * Writes the log with common labels and {@link Labels#FATAL} log level.
     *
     * @param line               Log message.
     * @param structuredMetadata Labels related to single log line only.
     * @since 1.1.1
     */
    void fatal(final String line, final Labels structuredMetadata) {
        fatal().log(line, structuredMetadata);
    }

    /**
     * Writes the log with common labels and {@link Labels#WARN} log level.
     *
     * @param line Log message.
     * @since 1.1.1
     */
    void warning(final String line) {
        warning().log(line);
    }

    /**
     * Writes the log with common labels and {@link Labels#WARN} log level.
     *
     * @param line               Log message.
     * @param structuredMetadata Labels related to single log line only.
     * @since 1.1.1
     */
    void warning(final String line, final Labels structuredMetadata) {
        warning().log(line, structuredMetadata);
    }

    /**
     * Writes the log with common labels and {@link Labels#INFO} log level.
     *
     * @param line Log message.
     * @since 1.1.1
     */
    void info(final String line) {
        info().log(line);
    }

    /**
     * Writes the log with common labels and {@link Labels#INFO} log level.
     *
     * @param line               Log message.
     * @param structuredMetadata Labels related to single log line only.
     * @since 1.1.1
     */
    void info(final String line, final Labels structuredMetadata) {
        info().log(line, structuredMetadata);
    }

    /**
     * Writes the log with common labels and {@link Labels#DEBUG} log level.
     *
     * @param line Log message.
     * @since 1.1.1
     */
    void debug(final String line) {
        debug().log(line);
    }

    /**
     * Writes the log with common labels and {@link Labels#DEBUG} log level.
     *
     * @param line               Log message.
     * @param structuredMetadata Labels related to single log line only.
     * @since 1.1.1
     */
    void debug(final String line, final Labels structuredMetadata) {
        debug().log(line, structuredMetadata);
    }

    /**
     * Writes the log with common labels and {@link Labels#VERBOSE} log level.
     *
     * @param line Log message.
     * @since 1.1.1
     */
    void verbose(final String line) {
        verbose().log(line);
    }

    /**
     * Writes the log with common labels and {@link Labels#VERBOSE} log level.
     *
     * @param line               Log message.
     * @param structuredMetadata Labels related to single log line only.
     * @since 1.1.1
     */
    void verbose(final String line, final Labels structuredMetadata) {
        verbose().log(line, structuredMetadata);
    }

    /**
     * Writes the log with common labels and {@link Labels#UNKNOWN} log level.
     *
     * @param line Log message.
     * @since 1.1.1
     */
    void unknown(final String line) {
        unknown().log(line);
    }

    /**
     * Writes the log with common labels and {@link Labels#UNKNOWN} log level.
     *
     * @param line               Log message.
     * @param structuredMetadata Labels related to single log line only.
     * @since 1.1.1
     */
    void unknown(final String line, final Labels structuredMetadata) {
        unknown().log(line, structuredMetadata);
    }
}
