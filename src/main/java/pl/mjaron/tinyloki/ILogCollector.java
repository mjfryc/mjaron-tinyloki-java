package pl.mjaron.tinyloki;

/**
 * Responsible for creating streams (with fixed labels) and collecting data from them.
 */
public interface ILogCollector {

    /**
     * Called before using the object.
     *
     * @param logListener The object used as callback when new log occurs.
     * @since 0.4.0
     */
    void configureLogListener(ILogListener logListener);

    /**
     * Called before using the object.
     *
     * @param bufferingManager The object which determines sent data buffering policy.
     * @since 0.4.0
     */
    void configureBufferingManager(IBuffering bufferingManager);

    /**
     * Configures all values in single command.
     *
     * @param logListener      The object used as callback when new log occurs.
     * @param bufferingManager The object which determines sent data buffering policy.
     * @since 0.4.0
     */
    default void configure(ILogListener logListener, IBuffering bufferingManager) {
        configureLogListener(logListener);
        configureBufferingManager(bufferingManager);
    }

    /**
     * Creates a new stream.
     *
     * @param labels Unique set of labels.
     * @return New stream instance.
     */
    ILogStream createStream(final Labels labels);

    /**
     * Gets data from streams and clears streams state.
     *
     * @return Encoded content of streams or null if there is no new logs to send.
     */
    byte[] collect();

    /**
     * Collect all logs, including logs buffered with {@link IBuffering}.
     *
     * @return Array of messages to be sent.
     */
    byte[][] collectAll();

    /**
     * HTTP Content-Type describing data type of collect() result.
     *
     * @return HTTP Content-Type header value.
     */
    String contentType();
}
