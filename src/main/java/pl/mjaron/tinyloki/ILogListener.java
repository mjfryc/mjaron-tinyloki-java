package pl.mjaron.tinyloki;

/**
 * Waits for logs from {@link ILogCollector}.
 * Depending on the observer policy, it may collect the logs or wait for more logs.
 */
public interface ILogListener {

    /**
     * Called by {@link ILogCollector} when new log occurs.
     *
     * @param cachedLogsCount Count of logs already cached in the {@link ILogCollector}.
     */
    void onLog(int cachedLogsCount);

    /**
     * Create the log listener which does nothing. Mainly for diagnostic purposes.
     *
     * @return The new dummy log listener.
     * @since 0.4.0
     */
    static ILogListener dummy() {
        //noinspection Convert2Lambda
        return new ILogListener() {
            @Override
            public void onLog(int cachedLogsCount) {
            }
        };
    }
}
