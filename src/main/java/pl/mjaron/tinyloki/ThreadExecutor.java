package pl.mjaron.tinyloki;

public class ThreadExecutor implements IExecutor, Runnable {

    public static final int DEFAULT_LOG_COLLECTION_PERIOD_MS = 5000;
    private final BlockingLogListener logListener = new BlockingLogListener();
    private LogController logController = null;
    private Thread workerThread = null;
    private int logCollectionPeriod = DEFAULT_LOG_COLLECTION_PERIOD_MS;

    public void setLogCollectionPeriod(final int logCollectionPeriod) {
        this.logCollectionPeriod = logCollectionPeriod;
    }

    @Override
    public void configure(LogController logController) {
        if (logController == null) {
            throw new NullPointerException("Cannot configure ThreadExecutor: Given log controller is null.");
        }
        this.logController = logController;
        this.logController.getLogCollector().setLogListener(this.logListener);
    }

    @Override
    public void start() {
        if (logController == null) {
            throw new RuntimeException("Cannot start the executor: The ThreadExecutor is not configured.");
        }
        workerThread = new Thread(this, "tinyloki." + this.getClass().getSimpleName());
        workerThread.start();
    }

    @Override
    public void stop(final int timeout) throws InterruptedException {
        if (workerThread != null) {
            workerThread.interrupt();
            workerThread.join(timeout);
        }
    }

    @Override
    public void sync(final int timeout) throws InterruptedException {
        logListener.flush(timeout);
    }

    @Override
    public void run() {
        while (true) {
            try {
                final int anyLogs = logListener.waitForLogs(logCollectionPeriod);
                if (anyLogs > 0) {
                    logController.internalProcessLogs();
                }
            } catch (final InterruptedException e) {
                logController.getLogMonitor().onWorkerThreadExit(true);
                return;
            } catch (final Exception e) {
                logController.getLogMonitor().onException(e);
            }
        }
    }
}
