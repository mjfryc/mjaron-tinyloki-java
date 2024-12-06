package pl.mjaron.tinyloki;

import java.util.Map;

/**
 * Organizes cooperation between collector and sender.
 * Method {@link #start()} creates worker thread which sends new logs, so should be called
 * when application starts.
 */
public class LogController {

    private static final int LOG_WAIT_TIME = 100;

    /**
     * Default maximum time of {@link #softStop()} operation in milliseconds..
     *
     * @deprecated Since <code>0.4.0</code>. Use {@link #DEFAULT_STOP_TIME} instead.
     */
    @Deprecated
    private static final int DEFAULT_SOFT_STOP_WAIT_TIME = 2000;

    /**
     * Default maximum time of {@link #hardStop()} operation in milliseconds.
     *
     * @deprecated Since <code>0.4.0</code>. Use {@link #DEFAULT_STOP_TIME} instead.
     */
    @Deprecated
    private static final int DEFAULT_HARD_STOP_WAIT_TIME = 1000;

    /**
     * Default wait time of {@link #stop()} operation in milliseconds.
     *
     * @see #stop()
     * @see #stop(int)
     * @since 0.4.0
     */
    public static final int DEFAULT_STOP_TIME = 1000;

    /**
     * Default wait time of {@link #sync()} operation in milliseconds.
     *
     * @see #sync()
     * @see #sync(int)
     * @since 0.4.0
     */
    public static final int DEFAULT_SYNC_TIME = 1000;

    private final ILogCollector logCollector;
    private final ILogEncoder logEncoder;
    private final ILogSender logSender;
    private final LabelSettings labelSettings;
    private final IExecutor executor;
    private final ILogMonitor logMonitor;

    /**
     * Main constructor designed for user of this library.
     *
     * @param logCollector      ILogCollector implementation, which is responsible for creating new streams and collecting its logs.
     * @param logEncoder        Optional (nullable) log encoder which is responsible for encode whole log message.
     * @param logSenderSettings {@link LogSenderSettings} used to initialize the {@link ILogSender log sender}.
     *                          Some settings will be overridden by this constructor.
     * @param logSender         Sends the logs collected by log controller.
     * @param labelSettings     Preferences of the {@link Labels}. See {@link LabelSettings}.
     * @param executor          The {@link IExecutor} implementation.
     * @param logMonitor        Handles diagnostic events from whole library.
     * @since 0.3.4
     */
    public LogController(final ILogCollector logCollector, ILogEncoder logEncoder, final LogSenderSettings logSenderSettings, final ILogSender logSender, final LabelSettings labelSettings, final IExecutor executor, final ILogMonitor logMonitor) {
        this.logCollector = logCollector;
        this.logEncoder = logEncoder;
        this.logSender = logSender;
        this.labelSettings = labelSettings;
        this.executor = executor;
        this.logMonitor = logMonitor;
        logSenderSettings.setContentType(this.logCollector.contentType());
        final String contentEncoding = (this.logEncoder == null) ? null : this.logEncoder.contentEncoding();
        logSenderSettings.setContentEncoding(contentEncoding);
        this.logSender.configure(logSenderSettings, logMonitor);
        this.executor.configure(this);
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
     * @param executor          The {@link IExecutor} implementation.
     * @param logMonitor        Handles diagnostic events from whole library.
     * @deprecated Use {@link LogController#LogController(ILogCollector, ILogEncoder, LogSenderSettings, ILogSender, LabelSettings, IExecutor, ILogMonitor)} instead, where logEncoder parameter should be specified explicitly.
     */
    public LogController(final ILogCollector logCollector, final LogSenderSettings logSenderSettings, final ILogSender logSender, final LabelSettings labelSettings, final IExecutor executor, final ILogMonitor logMonitor) {
        this(logCollector, null, logSenderSettings, logSender, labelSettings, executor, logMonitor);
    }

    /**
     * THe log monitor getter.
     *
     * @return The log monitor set in the constructor.
     * @since 0.4.0
     */
    public ILogMonitor getLogMonitor() {
        return this.logMonitor;
    }

    /**
     * Provides the {@link ILogCollector} instance used to initialize this <code>LogController</code>.
     *
     * @return The {@link ILogCollector} instance used to initialize this <code>LogController</code>.
     * @since 0.4.0
     */
    public ILogCollector getLogCollector() {
        return logCollector;
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
        executor.start();
        return this;
    }

    /**
     * Blocking function. Stops the executor sending the logs in the background.
     *
     * @param timeout Maximum time to wait for executor to finish.
     * @return This reference.
     * @since 0.4.0
     */
    public LogController stop(final int timeout) throws InterruptedException {
        executor.stop(timeout);
        return this;
    }

    /**
     * Blocking function. Stops the executor sending the logs in the background with default timeout defined as {@link #DEFAULT_STOP_TIME}.
     *
     * @return This reference.
     * @since 0.4.0
     */
    public LogController stop() throws InterruptedException {
        return stop(DEFAULT_STOP_TIME);
    }

    /**
     * Blocking function. Stops the flow until all already requested logs are processed (sent) or given time has passed.
     *
     * @param timeout The maximum time to wait in milliseconds.
     * @return This reference.
     * @since 0.4.0
     */
    public LogController sync(final int timeout) throws InterruptedException {
        executor.sync(timeout);
        return this;
    }

    /**
     * Blocking function. Stops the flow until all already requested logs are processed (sent) or given time has passed.
     * The maximum blocking time is defined by {@link #DEFAULT_SYNC_TIME}.
     *
     * @return This reference.
     * @since 0.4.0
     */
    public LogController sync() throws InterruptedException {
        executor.sync(DEFAULT_SYNC_TIME);
        return this;
    }

    /**
     * Request worker thread to do last jobs. This method is non-blocking.
     *
     * @return This reference.
     */
    @SuppressWarnings("UnusedReturnValue")
    synchronized public LogController softStopAsync() {
        /// TODO IMPLEMENTATION HERE.
        return this;
    }

    /**
     * Interrupts worker thread and tries to join for given interruptTimeout.
     *
     * @param interruptTimeout Timeout in milliseconds.
     * @return This reference.
     * @see #stop(int)
     * @see #stop()
     * @deprecated Since <code>0.4.0</code>. Use {@link #stop(int)} instead.
     */
    public LogController hardStop(final long interruptTimeout) {
        try {
            this.stop(java.lang.Math.toIntExact(interruptTimeout));
        } catch (Exception e) {
            logMonitor.onException(e);
        }
        return this;
    }

    /**
     * Blocking function. Request to stop worker thread by interrupting it. Waits for interruption with default timeout.
     *
     * @return This reference.
     * @see #hardStop(long)
     * @see #stop()
     * @see #stop(int)
     * @deprecated Since <code>0.4.0</code>. Use {@link #stop(int)} instead.
     */
    @Deprecated
    @SuppressWarnings("UnusedReturnValue")
    public LogController hardStop() {
        return this.hardStop(DEFAULT_HARD_STOP_WAIT_TIME);
    }

    /**
     * Blocking function. Tells to worker to send logs to this time point and exit.
     *
     * @param softTimeout Timeout in milliseconds.
     * @return This reference.
     * @see #stop()
     * @see #stop(int)
     * @see #sync(int)
     * @deprecated Since <code>0.4.0</code>. Use {@link #sync(int)} and {@link #stop(int)} instead.
     */
    @Deprecated
    public LogController softStop(final long softTimeout) {
        try {
            this.sync(java.lang.Math.toIntExact(softTimeout));
            this.stop();
        } catch (final Exception e) {
            logMonitor.onException(e);
        }
        return this;
    }

    /**
     * Blocking function. Soft stop with default timeout.
     *
     * @return This reference.
     * @see #softStop(long)
     * @see #stop()
     * @see #stop(int)
     * @see #sync(int)
     * @deprecated Since <code>0.4.0</code>. Use {@link #sync(int)} and {@link #stop(int)} instead.
     */
    @Deprecated
    @SuppressWarnings("unused")
    public LogController softStop() {
        return this.softStop(DEFAULT_SOFT_STOP_WAIT_TIME);
    }

    /**
     * Tells if worker thread has stopped softly, doing all its work before exiting.
     *
     * @return Always <code>true</code>.
     * @deprecated Since <code>0.4.0</code>. This method will be removed.
     */
    @Deprecated
    public boolean isSoftStopped() {
        return true;
    }

    /**
     * Used by the {@link IExecutor} to process logs in execution context (separate or borrowed thread, task, etc.).
     * Should not be called by the library client directly.
     *
     * @since 0.4.0
     */
    public void internalProcessLogs() throws InterruptedException {
        final byte[] logs = logCollector.collect();
        if (logs == null) {
            return;
        }
        if (logEncoder == null) {
            logSender.send(logs);
            return;
        }

        final byte[] encodedLogs = logEncoder.encode(logs);
        logMonitor.onEncoded(logs, encodedLogs);
        logSender.send(encodedLogs);
    }
}
