package pl.mjaron.tinyloki;

class BlockingLogListener implements ILogListener {

    private int cachedLogsCount = 0;
    private final Object cachedLogsMonitor = new Object();

    /**
     * Tells which sequence number of logs waiting is processed.
     */
    private long logEntryLevel = 0;

    /**
     * Describes the level of log sequence the client is waiting to complete the flush operation.
     */
    private long flushLevel = 0;

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
     * Wait for all logs in given time.
     *
     * @param timeout Time of waiting for logs.
     * @return Captured logs count.
     * @throws InterruptedException When calling thread has been interrupted.
     */
    public int waitForLogs(final int timeout) throws InterruptedException {
        synchronized (cachedLogsMonitor) {
            ++logEntryLevel;
            if (flushLevel <= logEntryLevel)
            {
                final long timePoint = Utils.MonotonicClock.timePoint(timeout);
                do {
                    if (Utils.MonotonicClock.waitUntil(cachedLogsMonitor, timePoint)) {
                        break;
                    }
                } while (flushLevel <= logEntryLevel);
            }

            return consumeCachedLogsUnsafe();
        }
    }

    /**
     * Request the {@link #waitForLogs(int)} to return asap.
     */
    public void flush(final int timeout) throws InterruptedException {
        final long timePoint = Utils.MonotonicClock.timePoint(timeout);
        synchronized (cachedLogsMonitor) {
            final long targetLevel = logEntryLevel + 2;
            flushLevel = targetLevel;
            cachedLogsMonitor.notifyAll();

            while (targetLevel > logEntryLevel) {
                if (Utils.MonotonicClock.waitUntil(cachedLogsMonitor, timePoint)) {
                    break;
                }
            }
        }
    }
}
