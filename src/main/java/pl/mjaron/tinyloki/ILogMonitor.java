package pl.mjaron.tinyloki;

/**
 * Handlers of logging events.
 */
public interface ILogMonitor {

    /**
     * Called before sending given data to HTTP server.
     *
     * @param message Data reference.
     */
    void send(final byte[] message);

    /**
     * Called on HTTP server response with good status.
     *
     * @param status HTTP status.
     */
    void sendOk(final int status);

    /**
     * Handle send HTTP response error.
     *
     * @param status  HTTP status code.
     * @param message HTTP status message.
     */
    void sendErr(final int status, final String message);

    /**
     * Called on any exception.
     *
     * @param exception Exception reference.
     */
    void onException(final Exception exception);

    /**
     * Called when worker thread exits.
     *
     * @param isSoft Tells whether worker thread has exited without interrupting.
     */
    void onWorkerThreadExit(final boolean isSoft);
}
