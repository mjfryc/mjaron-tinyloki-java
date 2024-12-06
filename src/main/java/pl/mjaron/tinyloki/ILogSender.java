package pl.mjaron.tinyloki;

/**
 * Sends the logs.
 *
 * @since 0.3.0
 */
public interface ILogSender {

    /**
     * Configures this sender. Called once by {@link LogController} when all parameters are determined.
     * <p>
     * This method should be idempotent.
     * <p>
     * Method {@link #send(byte[])} cannot be called without foregoing configuration.
     *
     * @param logSenderSettings {@link LogSenderSettings} instance.
     * @param logMonitor        {@link ILogMonitor} used for diagnostics and error handling.
     */
    void configure(LogSenderSettings logSenderSettings, ILogMonitor logMonitor);

    /**
     * Creates connection and sends given data by HTTP request.
     * Calls several {@link ILogMonitor} methods pointing what's the request data and HTTP response result.
     *
     * @param message Data to send in HTTP request content.
     * @throws RuntimeException On connection error.
     */
    void send(final byte[] message) throws InterruptedException;
}
