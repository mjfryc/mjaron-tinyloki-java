package pl.mjaron.tinyloki;

/**
 * Top-level log interface.
 * Single stream has fixed label values.
 */
public interface ILogStream {

    /**
     * Thread-safe method used to write log messages to a stream.
     *
     * @param timestampMs Usually System.currentTimeMillis().
     * @param line        Log content.
     */
    void log(final long timestampMs, final String line);

    /**
     * Thread-safe log line with current time.
     *
     * @param line Log content.
     */
    default void log(final String line) {
        log(System.currentTimeMillis(), line);
    }

    /**
     * Release log stream, so it isn't longer managed by its log collector.
     * It is not mandatory to call if this stream lifetime is the same as application lifetime.
     * <p>
     * Note that if stream is released too fast after logging,
     * the logs may be not consumed by {@link IExecutor} and not sent to the server.
     */
    void release();
}
