package pl.mjaron.tinyloki;

public class ThreadExecutor implements IExecutor, Runnable {

    public static final int DEFAULT_LOG_COLLECTION_PERIOD_MS = 5000;

    private final BlockingLogListener logListener = new BlockingLogListener();
    private LogController logController = null;
    private Thread workerThread = null;
    private int logCollectionPeriod = DEFAULT_LOG_COLLECTION_PERIOD_MS;

    public ThreadExecutor() {
    }

    public ThreadExecutor(final int logCollectionPeriod) {
        this.logCollectionPeriod = logCollectionPeriod;
    }

    public int getLogCollectionPeriod() {
        return logCollectionPeriod;
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
        synchronized (this) {
            if (workerThread != null) {
                throw new RuntimeException("Cannot start the executor: Already started.");
            }
            workerThread = new Thread(this, "tinyloki." + this.getClass().getSimpleName());
            workerThread.start();
        }
    }

    @Override
    public boolean stop(final int timeout) throws InterruptedException {
        final Thread thread;
        synchronized (this) {
            if (workerThread == null) { // There is no worker thread, returning true what means that thread is stopped.
                return true;
            }
            thread = workerThread;
        }

        thread.interrupt();
        thread.join(timeout);

        // If nobody set worker thread to null yet, let's set the thread to null.
        synchronized (this) {
            if (workerThread != null) {
                assert workerThread == thread;
                workerThread = null;
            }
        }
        return thread.getState() == Thread.State.TERMINATED;
    }

    @Override
    public void stopAsync() {
        synchronized (this) {
            if (workerThread != null) {
                workerThread.interrupt();
            }
        }
    }

    @Override
    public boolean sync(final int timeout) throws InterruptedException {
        return logListener.sync(timeout);
    }

    @Override
    public void run() {
        logController.getLogMonitor().logInfo("Worker thread: started.");

        while (true) {
            try {
                final int anyLogs = logListener.waitForLogs(logCollectionPeriod);
                if (anyLogs > 0) {
                    logController.internalProcessLogs();
                }
            } catch (final InterruptedException e) {
                logController.getLogMonitor().logInfo("Worker thread: exit.");
                return;
            } catch (final Exception e) {
                logController.getLogMonitor().onException(e);
            }
        }
    }
}
