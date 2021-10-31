package pl.mjaron.tinyloki;

/**
 * Top-level log interface with fixed static labels.
 */
public interface ILogStream {

    /**
     * Thread-safe method used to write log messages to a stream.
     * @param timestampMs Usually System.currentTimeMillis().
     * @param line Log content.
     */
    void log(final long timestampMs, final String line);

    /**
     * Release log stream, so it isn't longer managed by its log collector.
     * It is not mandatory to call if log lifetime is the same as application lifetime.
     */
    void release();
}
