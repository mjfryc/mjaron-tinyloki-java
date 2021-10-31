package pl.mjaron.tinyloki;

/**
 * Handlers of logging events.
 */
public interface ILogMonitor {

    void send(final byte[] message);

    void sendOk(final int status);

    /**
     * Handle send HTTP response error.
     */
    void sendErr(final int status, final String message);

    /**
     * Called on any exception.
     * @param exception
     */
    void onException(final Exception exception);
}
