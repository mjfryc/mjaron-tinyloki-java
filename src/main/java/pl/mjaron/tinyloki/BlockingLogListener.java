package pl.mjaron.tinyloki;

class BlockingLogListener implements ILogListener {

    private final Object cachedLogsMonitor = new Object();
    private int cachedLogsCount = 0;
    /**
     * Tells which sequence number of logs waiting is processed.
     */
    private long logEntryLevel = 0;

    /**
     * Describes the level of log sequence the client is waiting to complete the syncAnd operation.
     */
    private long syncLevel = 0;

    @Override
    public void onLog(final int cachedLogsCount) {
        synchronized (cachedLogsMonitor) {
            this.cachedLogsCount = cachedLogsCount;
        }
    }

    /**
     * Consume the cached logs without thread safety. For internal use.
     *
     * @return Cached logs count.
     */
    private int consumeCachedLogsUnsafe() {
        final int cachedLogsCopy = cachedLogsCount;
        cachedLogsCount = 0;
        return cachedLogsCopy;
    }

    /**
     * Waits for logs in given time.
     *
     * @param time Time of waiting for logs.
     * @return Captured logs count.
     * @throws InterruptedException When calling thread has been interrupted.
     * @implNote Collects logs until:
     * <ul>
     * <li>given time passes or</li>
     * <li>{@link #syncLevel} exceeds the current {@link #logEntryLevel} which means that this function must be called again to increase the {@link #logEntryLevel}.</li>
     * </ul>
     */
    public int waitForLogs(final int time) throws InterruptedException {
        synchronized (cachedLogsMonitor) {
            ++logEntryLevel;
            cachedLogsMonitor.notifyAll(); // Notify about updated sync level.

            // If Synchronization not requested, just wait for logs as long as possible (with given time).
            if (syncLevel <= logEntryLevel) {

                // Define the waiting deadline time point.
                final long timePoint = Utils.MonotonicClock.timePoint(time);
                do {
                    if (Utils.MonotonicClock.waitUntil(cachedLogsMonitor, timePoint)) {

                        // If timeout occurs, break the waiting and return the real consumed logs count.
                        break;
                    }

                    // If Synchronization not requested, just wait for logs as long as possible (with given time).
                } while (syncLevel <= logEntryLevel);
            }

            return consumeCachedLogsUnsafe();
        }
    }

    /**
     * Request the {@link #waitForLogs(int)} to return asap.
     *
     * @return <code>true</code> When the synchronization operation has passed with success and all logs has been synchronized.
     * <p/>
     * <code>false</code> When all or some logs hasn't been synchronized.
     */
    public boolean sync(final int timeout) throws InterruptedException {

        // Determine the deadline time point when the sync operation must finish.
        final long timePoint = Utils.MonotonicClock.timePoint(timeout);

        synchronized (cachedLogsMonitor) {

            // Determine the level until the sync operation will block.
            final long targetLevel = logEntryLevel + 2;
            syncLevel = targetLevel;

            cachedLogsMonitor.notifyAll(); // Notify about updated sync level.

            // Wait until the logEntryLevel reaches the requested target level.
            while (targetLevel > logEntryLevel) {

                // If timeout occurs, return information whether the target level has been achieved.
                if (Utils.MonotonicClock.waitUntil(cachedLogsMonitor, timePoint)) {
                    return false; /// Failed to wait - the time point has been achieved.
                }
            }

            // The loop has finished without a timeout. The target level has been achieved.
            return true;
        }
    }
}
