package pl.mjaron.tinyloki;

/**
 * Handlers of logging events.
 */
public interface ILogMonitor {

    /**
     * Default meta-logging implementation.
     * <p>
     * Used internally by TinyLoki implementation for this library diagnostic purposes.
     *
     * @param what Log content.
     * @since 1.1.7
     */
    static void printVerbose(final String what) {
        System.out.println("[TinyLoki][" + System.currentTimeMillis() + "][V] " + what);
    }

    /**
     * Default meta-logging implementation.
     * <p>
     * Used internally by TinyLoki implementation for this library diagnostic purposes.
     *
     * @param what Log content.
     */
    static void printInfo(final String what) {
        System.out.println("[TinyLoki][" + System.currentTimeMillis() + "][I] " + what);
    }

    /**
     * Default meta-logging implementation.
     * <p>
     * Used internally by TinyLoki implementation for this library diagnostic purposes.
     *
     * @param what Log content.
     */
    static void printError(final String what) {
        System.err.println("[TinyLoki][" + System.currentTimeMillis() + "][E] " + what);
    }

    /**
     * Tells whether verbose messages are logged by this log monitor.
     *
     * @return <code>true</code> when verbose messages are processed by this log monitor.
     * @since 1.1.7
     */
    boolean isVerbose();

    void logVerbose(final String what);

    void logInfo(final String what);

    void logError(final String what);

    /**
     * Called when {@link TinyLoki} is set up and ready to work.
     *
     * @param contentType     Content type used by {@link ILogCollector}.
     * @param contentEncoding Content encoding used by {@link ILogEncoder}.
     */
    void onConfigured(final String contentType, final String contentEncoding);

    /**
     * Called when HTTP message content has been encoded.
     *
     * @param in  Data before encoding.
     * @param out Data after encoding.
     */
    void onEncoded(final byte[] in, final byte[] out);

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
     * Called when sync operation ends.
     *
     * @param isSuccess The sync operation result.
     * @since 1.0.0
     */
    void onSync(final boolean isSuccess);

    /**
     * Called when {@link IExecutor} has started.
     *
     * @since 1.0.0
     */
    void onStart();

    /**
     * Called when {@link IExecutor} has stopped.
     *
     * @param isSuccess The stop operation result.
     * @since 1.0.0
     */
    void onStop(final boolean isSuccess);
}
