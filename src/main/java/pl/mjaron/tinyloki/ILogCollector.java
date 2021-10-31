package pl.mjaron.tinyloki;

import java.util.Map;

/**
 * Responsible for creating streams (with fixed labels) and collecting data from them.
 */
public interface ILogCollector {

    /**
     * Creates a new stream.
     * @param labels Unique set of labels.
     * @return New stream instance.
     */
    ILogStream createStream(final Map<String, String> labels);

    /**
     * Gets data from streams and clears streams state.
     * @return Encoded content of streams.
     */
    byte[] collect();

    /**
     * HTTP content type describing data type of collect() result.
     * @return
     */
    String contentType();

    /**
     * Stop thread until a new log will occur.
     * @param timeout Time in milliseconds.
     * @return True if any logs has occurred in given time.
     * @throws InterruptedException When given thread has been interrupted.
     */
    boolean waitForLogs(final long timeout) throws InterruptedException;
}
