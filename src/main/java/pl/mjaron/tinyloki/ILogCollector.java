package pl.mjaron.tinyloki;

/**
 * Responsible for creating streams (with fixed labels) and collecting data from them.
 */
public interface ILogCollector {

    /**
     * Called before using the object.
     *
     * @param logListener The object used as callback when new log occurs.
     */
    void setLogListener(ILogListener logListener);

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
     * HTTP Content-Type describing data type of collect() result.
     *
     * @return HTTP Content-Type header value.
     */
    String contentType();
}
