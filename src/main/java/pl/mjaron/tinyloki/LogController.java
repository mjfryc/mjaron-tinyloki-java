package pl.mjaron.tinyloki;

import java.util.Map;

/**
 * Organizes cooperation between collector and sender.
 * Method {@link #start()} creates worker thread which sends new logs, so should be called
 * when application starts.
 */
public class LogController {

    private static final long LOG_WAIT_TIME = 100;
    private static final long DEFAULT_SOFT_STOP_WAIT_TIME = 2000;
    private static final long DEFAULT_HARD_STOP_WAIT_TIME = 1000;

    private final ILogCollector logCollector;
    private final ILogEncoder logEncoder;
    private final ILogSender logSender;
    private final LabelSettings labelSettings;
    private final ILogMonitor logMonitor;
    private Thread workerThread = null;
    private boolean softFinishing = false;
    private boolean softExit = false;

    /**
     * Main constructor designed for user of this library.
     *
     * @param logCollector      ILogCollector implementation, which is responsible for creating new streams and collecting its logs.
     * @param logEncoder        Optional (may be null) log encoder which is responsible for encode whole log message.
     * @param logSenderSettings {@link LogSenderSettings} used to initialize the {@link ILogSender log sender}.
     *                          Some settings will be overridden by this constructor.
     * @param logSender         Sends the logs collected by log controller.
     * @param labelSettings     Preferences of the {@link Labels}. See {@link LabelSettings}.
     * @param logMonitor        Handles diagnostic events from whole library.
     * @since 0.3.4
     */
    public LogController(final ILogCollector logCollector, ILogEncoder logEncoder, final LogSenderSettings logSenderSettings, final ILogSender logSender, final LabelSettings labelSettings, final ILogMonitor logMonitor) {
        this.logCollector = logCollector;
        this.logEncoder = logEncoder;
        this.logSender = logSender;
        this.labelSettings = labelSettings;
        this.logMonitor = logMonitor;
        logSenderSettings.setContentType(this.logCollector.contentType());
        final String contentEncoding = (this.logEncoder == null) ? null : this.logEncoder.contentEncoding();
        logSenderSettings.setContentEncoding(contentEncoding);
        this.logSender.configure(logSenderSettings, logMonitor);
        this.logMonitor.onConfigured(this.logCollector.contentType(), contentEncoding);
    }

    /**
     * Maintenance constructor designed for user of this library.
     *
     * @param logCollector      ILogCollector implementation, which is responsible for creating new streams and collecting its logs.
     * @param logSenderSettings {@link LogSenderSettings} used to initialize the {@link ILogSender log sender}.
     *                          Some settings will be overridden by this constructor.
     * @param logSender         Sends the logs collected by log controller.
     * @param labelSettings     Preferences of the {@link Labels}. See {@link LabelSettings}.
     * @param logMonitor        Handles diagnostic events from whole library.
     * @deprecated Use {@link LogController#LogController(ILogCollector, ILogEncoder, LogSenderSettings, ILogSender, LabelSettings, ILogMonitor)} instead, where logEncoder parameter should be specified explicitly.
     */
    public LogController(final ILogCollector logCollector, final LogSenderSettings logSenderSettings, final ILogSender logSender, final LabelSettings labelSettings, final ILogMonitor logMonitor) {
        this(logCollector, null, logSenderSettings, logSender, labelSettings, logMonitor);
    }

    /**
     * Creates new stream from log collector.
     *
     * @param labels Static labels. There shouldn't be many streams with the same labels combination.
     * @return New stream reference.
     */
    @SuppressWarnings("unused")
    public ILogStream createStream(final Map<String, String> labels) {
        return this.createStream(Labels.from(labels));
    }

    /**
     * Creates new stream from log collector.
     *
     * @param labels Static labels. There shouldn't be many streams with the same labels combination.
     * @return New stream reference.
     */
    public ILogStream createStream(final Labels labels) {
        return logCollector.createStream(Labels.prettify(labels, labelSettings));
    }

    /**
     * Provides a {@link StreamBuilder} to initialize the stream. E.g:
     *
     * <pre>{@code
     * ILogStream myStream = logController.stream().info().l("my_custom_label", "value").build();
     * }</pre>
     *
     * @return New instance of {@link StreamBuilder}.
     */
    public StreamBuilder stream() {
        return new StreamBuilder(this);
    }

    /**
     * Starts worker thread which is responsible for collecting and sending logs.
     *
     * @return This reference.
     */
    public LogController start() {
        workerThread = new Thread("LogController.workerThread") {
            @Override
            public void run() {
                workerLoop();
            }
        };
        workerThread.start();
        return this;
    }

    /**
     * Request worker thread to do last jobs. This method is non-blocking.
     *
     * @return This reference.
     */
    @SuppressWarnings("UnusedReturnValue")
    synchronized public LogController softStopAsync() {
        softFinishing = true;
        return this;
    }

    /**
     * Tells whether worker thread exited or not.
     *
     * @return True if worker thread is terminated.
     */
    public boolean isHardStopped() {
        //return workerThread.isAlive();
        return (workerThread.getState() == Thread.State.TERMINATED);
    }

    /**
     * Interrupts worker thread and tries to join for given interruptTimeout.
     *
     * @param interruptTimeout Timeout in milliseconds.
     * @return This reference.
     */
    public LogController hardStop(final long interruptTimeout) {
        try {
            this.softStopAsync();
            workerThread.interrupt();
            workerThread.join(interruptTimeout);
        } catch (InterruptedException e) {
            logMonitor.onException(e);
        }
        return this;
    }

    /**
     * Blocking function. Request to stop worker thread by interrupting it. Waits for interruption with default timeout.
     *
     * @return This reference.
     * @see #hardStop(long)
     */
    @SuppressWarnings("UnusedReturnValue")
    public LogController hardStop() {
        return this.hardStop(DEFAULT_HARD_STOP_WAIT_TIME);
    }

    /**
     * Blocking function. Tells to worker to send logs to this time point and exit.
     *
     * @param softTimeout Timeout in milliseconds.
     * @return This reference.
     */
    public LogController softStop(final long softTimeout) {
        try {
            this.softStopAsync();
            workerThread.join(softTimeout);
        } catch (final InterruptedException e) {
            logMonitor.onException(e);
        }
        return this;
    }

    /**
     * Blocking function. Soft stop with default timeout.
     *
     * @return This reference.
     * @see #softStop(long)
     */
    @SuppressWarnings("unused")
    public LogController softStop() {
        return this.softStop(DEFAULT_SOFT_STOP_WAIT_TIME);
    }

    /**
     * Tells if worker thread has stopped softly, doing all its work before exiting.
     *
     * @return True if worker thread has exited without interruption.
     */
    public boolean isSoftStopped() {
        synchronized (this) {
            return this.softExit;
        }
    }

    /**
     * Defines worker thread activity.
     */
    public void workerLoop() {
        while (true) {
            try {
                boolean doLastCheck = false;
                synchronized (this) {
                    if (softFinishing) {
                        doLastCheck = true;
                    }
                }
                final int anyLogs;
                if (doLastCheck) {
                    anyLogs = logCollector.waitForLogs(1);
                } else {
                    anyLogs = logCollector.waitForLogs(LOG_WAIT_TIME);
                }
                if (anyLogs > 0) {
                    final byte[] logs = logCollector.collect();
                    if (logs != null) {
                        if (logEncoder != null) {
                            final byte[] encodedLogs = logEncoder.encode(logs);
                            logMonitor.onEncoded(logs, encodedLogs);
                            logSender.send(encodedLogs);
                        } else {
                            logSender.send(logs);
                        }
                    }
                }
                if (doLastCheck) {
                    synchronized (this) {
                        softExit = true;
                    }
                    logMonitor.onWorkerThreadExit(true);
                    return;
                }
            } catch (final InterruptedException e) {
                logMonitor.onException(e);
                logMonitor.onWorkerThreadExit(false);
                return;
            } catch (final Exception e) {
                logMonitor.onException(e);
            }
        }
    }
}
