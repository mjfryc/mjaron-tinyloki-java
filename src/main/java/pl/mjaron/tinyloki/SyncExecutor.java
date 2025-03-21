package pl.mjaron.tinyloki;

/**
 * The executor which do everything synchronously in calling thread.
 * It causes that  calling thread may be blocked.
 * <p>
 * For diagnostic purposes only.
 * <p>
 * Note: In case of thread interruption, the {@link RuntimeException} will be thrown.
 *
 * @since 1.0.0
 */
public class SyncExecutor implements IExecutor {

    private ILogProcessor logProcessor = null;

    @Override
    synchronized public void onLog(int cachedLogsCount) {
        try {
            logProcessor.processLogs();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void configure(ILogCollector logCollector, ILogProcessor logProcessor, ILogMonitor logMonitor) {
        this.logProcessor = logProcessor;
    }

    @Override
    public void start() {
    }

    @Override
    public boolean sync(int timeout) {
        return true;
    }

    @Override
    public void flush() {
    }

    @Override
    public boolean stop(int timeout) {
        return true;
    }

    @Override
    public void stopAsync() {
    }
}
