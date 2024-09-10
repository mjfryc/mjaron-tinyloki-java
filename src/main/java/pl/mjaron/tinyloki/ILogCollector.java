package pl.mjaron.tinyloki;

/**
 * Responsible for creating streams (with fixed labels) and collecting data from them.
 */
public interface ILogCollector {

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

    /**
     * Stop thread until a new log will occur.
     *
     * @param timeout Time in milliseconds.
     * @return Count of logs in given time. It may not be exact count of logs and depends on implementation.
     * @throws InterruptedException When given thread has been interrupted.
     */
    int waitForLogs(final long timeout) throws InterruptedException;
}
