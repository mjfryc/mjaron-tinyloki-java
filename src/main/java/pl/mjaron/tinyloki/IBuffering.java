package pl.mjaron.tinyloki;

/**
 * Responsible for organizing the data in messages which are restricted by size limit.
 * Useful to avoid exceeding the message size.
 * E.g. when the data sent is too big, the Grafana Loki server responds with:
 * <pre>
 * {@code
 *     HTTP response code: [500]
 *     error: [rpc error: code = ResourceExhausted desc = grpc: received message larger than max (6331143 vs. 4194304)]
 * }
 * </pre>
 * <p>
 * This class is <b>not</b> thread safe and should be called synchronously with implementation of {@link ILogCollector}.
 *
 * @see JsonLogCollector
 * @since 0.4.0
 */
public interface IBuffering {

    /**
     * The default value used to configure maximum size of message.
     */
    int DEFAULT_MAX_MESSAGE_SIZE = 2097152; // 2 * 1024 * 1024; // 2 MB.

    /**
     * The default value of configured message buffers.
     */
    int DEFAULT_MAX_BUFFERS_COUNT = 8;

    /**
     * Initializes the common options of buffering.
     *
     * @param logCollector    The collector instance used to gain all logs wit {@link ILogCollector#collect()}.
     * @param maxMessageSize  The max buffer size. This object will call {@link ILogCollector#collect()} to avoid buffer overflow.
     * @param maxBuffersCount The maximum buffers count. If this buffer count is exceeded, older buffers will be deleted.
     * @param executor        The {@link IExecutor} used to call {@link IExecutor#flush()} when buffer is full.
     * @param logMonitor      The {@link ILogMonitor} for diagnostic purposes.
     * @since 0.4.0
     */
    void configure(final ILogCollector logCollector, final int maxMessageSize, final int maxBuffersCount, final IExecutor executor, final ILogMonitor logMonitor);

    /**
     * Determines if the log should be accepted by the stream.
     *
     * @param logCandidateSize Size of the message which is about to be logged.
     * @return <c>true</c> if the message will be processed, <c>false</c> if the message should be ignored.
     * @since 0.4.0
     */
    boolean beforeLog(int logCandidateSize);

    /**
     * Notify about real accepted log size.
     *
     * @param logSize The final size of the accepted log.
     * @since 0.4.0
     */
    void logAccepted(int logSize);

    /**
     * Collect all - buffered and not already buffered logs (calls {@link ILogCollector#collect()} internally.
     *
     * @return Array of messages (byte arrays) to be sent.
     * @since 0.4.0
     */
    byte[][] collectAll();
}
