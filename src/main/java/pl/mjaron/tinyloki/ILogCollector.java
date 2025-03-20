package pl.mjaron.tinyloki;

/**
 * Responsible for creating streams (with fixed labels) and collecting data from them.
 */
public interface ILogCollector {

    /**
     * Called before using the object.
     *
     * @param logListener The object used as callback when new log occurs.
     * @since 1.0.0
     */
    void configureLogListener(ILogListener logListener);

    /**
     * Called before using the object.
     *
     * @param bufferingManager The object which determines sent data buffering policy.
     * @since 1.0.0
     */
    void configureBufferingManager(IBuffering bufferingManager);

    /**
     * If label settings are not <code>null</code>, the structured metadata is supported, else structured metadata should not be collected.
     *
     * @param structuredMetadataLabelSettings Settings for structured metadata validation.
     * @since 1.1.0
     */
    void configureStructuredMetadata(LabelSettings structuredMetadataLabelSettings);

    /**
     * Configures all values in single command.
     *
     * @param logListener      The object used as callback when new log occurs.
     * @param bufferingManager The object which determines sent data buffering policy.
     * @since 1.0.0
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
