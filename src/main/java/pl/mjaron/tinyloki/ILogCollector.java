package pl.mjaron.tinyloki;

/**
 * Responsible for creating streams (with fixed labels) and collecting data from them.
 */
public interface ILogCollector {

    /**
     * Called internally by {@link TinyLoki} to configure the object before using it.
     *
     * @param logListener                     The object used as callback when new log occurs.
     * @param bufferingManager                The object which determines sent data buffering policy.
     * @param structuredMetadataLabelSettings Settings for structured metadata validation.
     * @param timestampProviderFactory        The factory creating {@link ITimestampProvider} objects.
     * @since 1.1.3
     */
    void configure(ILogListener logListener, IBuffering bufferingManager, LabelSettings structuredMetadataLabelSettings, ITimestampProviderFactory timestampProviderFactory);

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
