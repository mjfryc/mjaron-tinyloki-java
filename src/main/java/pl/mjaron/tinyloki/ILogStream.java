package pl.mjaron.tinyloki;

/**
 * Represents a Grafana Loki <a href="https://grafana.com/docs/loki/v3.4.x/get-started/overview/#loki-overview">stream</a>.
 * Top-level log interface.
 * <p>
 * Single stream has fixed {@link Labels}.
 * <p>
 * Optionally, custom <a href="https://grafana.com/docs/loki/v3.4.x/get-started/labels/structured-metadata/">structured metadata</a> may be added to the particular log entry.
 */
public interface ILogStream {

    /**
     * Specifies that the stream should determine the current timestamp itself.
     *
     * @since 1.1.3
     */
    long TIMESTAMP_NONE = -1;

    /**
     * Thread-safe method used to write log messages to a stream.
     *
     * @param timestampNs        Log timestamp in <b>nanoseconds</b>.
     * @param line               Log content.
     * @param structuredMetadata The optional (nullable) {@link Labels} containing <a href="https://grafana.com/docs/loki/v3.4.x/get-started/labels/structured-metadata/">structured metadata</a> values.
     * @since 1.1.3
     */
    void log(final long timestampNs, final String line, final Labels structuredMetadata);

    /**
     * Thread-safe log line with custom time.
     *
     * @param timestampNs Log timestamp in <b>nanoseconds</b>.
     * @param line        Log content.
     * @since 1.1.3
     */
    default void log(final long timestampNs, final String line) {
        log(timestampNs, line, null);
    }

    /**
     * Thread-safe log line with current time.
     *
     * @param line Log content.
     */
    default void log(final String line) {
        log(TIMESTAMP_NONE, line, null);
    }

    /**
     * Thread-safe log line with current time and structured metadata.
     *
     * @param line               Log content.
     * @param structuredMetadata The optional (nullable) {@link Labels} containing <a href="https://grafana.com/docs/loki/v3.4.x/get-started/labels/structured-metadata/">structured metadata</a> values.
     * @since 1.1.0
     */
    default void log(final String line, final Labels structuredMetadata) {
        log(TIMESTAMP_NONE, line, structuredMetadata);
    }

    /**
     * Release log stream, so it isn't longer managed by its log collector.
     * It is not mandatory to call if this stream lifetime is the same as application lifetime.
     * <p>
     * Note that if stream is released too fast after logging,
     * the logs may be not consumed by {@link IExecutor} and not sent to the server.
     * <p>
     * If needed, call {@link TinyLoki#sync()} to synchronize all logs occurring up to this point.
     */
    void release();
}
