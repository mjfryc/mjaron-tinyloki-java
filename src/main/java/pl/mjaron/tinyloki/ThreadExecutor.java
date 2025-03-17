package pl.mjaron.tinyloki;

public class ThreadExecutor implements IExecutor, Runnable {

    public static final int DEFAULT_LOG_COLLECTION_PERIOD_MS = 5000;

    private final BlockingLogListener logListener = new BlockingLogListener();
    private ILogMonitor logMonitor = null;
    private ILogCollector logCollector = null;
    private ILogProcessor logProcessor = null;
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
    public void configure(final ILogCollector logCollector, final ILogProcessor logProcessor, final ILogMonitor logMonitor) {
        if (logCollector == null) {
            throw new NullPointerException("Cannot configure ThreadExecutor: Given log collector is null.");
        }

        if (logProcessor == null) {
            throw new NullPointerException("Cannot configure ThreadExecutor: Given log processor is null.");
        }

        if (logMonitor == null) {
            throw new NullPointerException("Cannot configure ThreadExecutor: Given log monitor is null.");
        }

        // Input parameters are valid - configuration is possible.
        logCollector.configureLogListener(this.logListener);

        // Configuration passed, saving changes.
        this.logMonitor = logMonitor;
        this.logCollector = logCollector;
        this.logProcessor = logProcessor;
    }

    @Override
    public void start() {
        if (logCollector == null) {
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

        if (thread.getState() != Thread.State.TERMINATED) {
            return false;
        }

        // If nobody set worker thread to null yet, let's set the thread to null.
        synchronized (this) {
            if (workerThread == thread) {
                workerThread = null;
            }
        }
        return true;
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
    public void flush() {
        logListener.flush();
    }

    @Override
    public void run() {
        logMonitor.logInfo("Worker thread: started.");

        while (true) {
            try {
                final int anyLogs = logListener.waitForLogs(logCollectionPeriod);
                if (anyLogs > 0) {
                    logProcessor.processLogs();
                }
            } catch (final InterruptedException e) {
                logMonitor.logInfo("Worker thread: interrupted.");
                return;
            } catch (final Exception e) {
                logMonitor.onException(e);
            }
        }
    }
}
