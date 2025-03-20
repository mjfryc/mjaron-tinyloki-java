package pl.mjaron.tinyloki;

import java.io.IOException;
import java.util.Map;

/**
 * The log controller which initializes the whole library and manages all the log processing.
 * <p>
 * <strong>Library initialization</strong>
 * <ul>
 *     <li>Create the instance of library log controller by calling {@link #withUrl(String)} static method.</li>
 *     <li>Initialize the log streams. Any stream is just a set of fixed key-value pairs assigned to the logs.</li>
 *     <li>Application flow, logging here...</li>
 *     <li>Close the {@code TinyLoki} library.</li>
 * </ul>
 *
 * <pre>{@code
 *  TinyLoki tinyLoki = TinyLoki.withUrl("http://localhost:3100")
 *      .withBasicAuth("user", "pass")
 *      .start();
 *
 *      ILogStream windowStream = tinyLoki.stream().info().l("device", "window").build();
 *      ILogStream doorStream = tinyLoki.stream().info().l("device", "door").build();
 *
 *      windowStream.log("The window is open.");
 *      doorStream.log("The door is open.");
 *
 *      // Time for syncing and closing the logs.
 *      boolean closedWithSuccess = tinyLoki.closeSync(1000);
 *
 *      System.out.println("Closed with success: " + closedWithSuccess);
 * }</pre>
 */
public class TinyLoki implements java.io.Closeable {

    /**
     * The Grafana Loki log insertion endpoint.
     * <p>
     * See: <a href="https://grafana.com/docs/loki/latest/reference/loki-http-api/#ingest-logs">Loki HTTP API / Ingest logs</a>
     *
     * @since 1.0.0
     */
    public static final String API_PUSH = "loki/api/v1/push";

    /**
     * Default wait time of {@link #stop()} operation in milliseconds.
     *
     * @see #stop()
     * @see #stop(int)
     * @since 1.0.0
     */
    public static final int DEFAULT_STOP_TIMEOUT = 1000;
    /**
     * Default wait time of {@link #sync()}} operation in milliseconds.
     *
     * @see #sync()
     * @see #sync(int)
     * @since 1.0.0
     */
    public static final int DEFAULT_SYNC_TIMEOUT = 1000;

    /**
     * Default maximum time of {@link #softStop()} operation in milliseconds.
     *
     * @deprecated Since <code>1.0.0</code>. Use {@link #DEFAULT_STOP_TIMEOUT} instead.
     */
    @Deprecated
    private static final int DEFAULT_SOFT_STOP_WAIT_TIME = 2000;
    /**
     * Default maximum time of {@link #hardStop()} operation in milliseconds.
     *
     * @deprecated Since <code>1.0.0</code>. Use {@link #DEFAULT_STOP_TIMEOUT} instead.
     */
    @Deprecated
    private static final int DEFAULT_HARD_STOP_WAIT_TIME = 1000;

    /**
     * Recommended way to initialize {@link TinyLoki} log controller.
     * <p>
     * Creates a new {@link Settings} object which is used to initialize a new {@link TinyLoki} instance, e.g:
     * <pre>
     *     LogController logController =
     *         TinyLoki.withUrl(url)    // Settings created here.
     *             .withBasicAuth(user, pass)
     *             .withConnectTimeout(connectTimeout)
     *             .withLogCollector(logCollector)
     *             .withLogMonitor(logMonitor)
     *             .start();            // LogController created here.
     * </pre>
     * <p>
     * Optionally call {@link #withExactUrl(String)} instead to skip URL value normalization
     * and use given URL directly as the endpoint of pushing logs.
     *
     * @param url URL to Loki HTTP API endpoint, usually ending with <code>/loki/api/v1/push</code>.
     * @return New instance of {@link Settings} object, initialized with given <code>url</code>.
     * @see #withExactUrl(String)
     * @since 0.3.0
     */
    public static Settings withUrl(final String url) {
        return Settings.fromUrl(url);
    }

    /**
     * Creates {@link Settings} instance with given URL value which will be directly as the endpoint of pushing logs.
     * <p>
     * By convention, use {@link #withUrl(String)} instead.
     *
     * @param url URL to Loki HTTP API endpoint, usually ending with <code>/loki/api/v1/push</code>.
     * @return New instance of {@link Settings} object, initialized with given <code>url</code>.
     * @see #withUrl(String)
     * @since 1.0.0
     */
    public static Settings withExactUrl(final String url) {
        return Settings.fromExactUrl(url);
    }

    /**
     * Creates a new {@link TinyLoki} with given {@link Settings}.
     * <p>
     * Use link {@link #withUrl(String)} which is the recommended way to initialize a new log controller instance.
     *
     * @param settings Configuration of a newly created {@link TinyLoki}.
     * @return New instance of {@link TinyLoki}.
     * @see #withUrl(String)
     * @since 0.3.0
     */
    public static TinyLoki createAndStart(final Settings settings) {
        return new TinyLoki(settings.getLogCollector(), settings.getLogEncoder(), settings.getLogSenderSettings(), settings.getLogSender(), settings.getLabelSettings(), settings.getStructuredMetadataLabelSettings(), settings.getExecutor(), settings.getBuffering(), settings.getLogMonitor()).start();
    }

    /**
     * Creates a basic configuration of LogController.
     *
     * @param url  URL to Loki HTTP API endpoint, usually ending with <code>/loki/api/v1/push</code>.
     * @param user Basic authentication user. If null, BA header will not be sent.
     * @param pass Basic authentication password. If null, BA header will not be sent.
     * @return New {@link TinyLoki LogController} object.
     * @deprecated Use {@link TinyLoki#withUrl(String)} to initialize settings and finally call {@link Settings#start()}, e.g:
     * <pre>
     *     TinyLoki.withUrl(url).withBasicAuth(user, pass).start();
     * </pre>
     */
    public static TinyLoki createAndStart(final String url, final String user, final String pass) {
        return TinyLoki.withUrl(url).withBasicAuth(user, pass).start();
    }

    private final ILogCollector logCollector;
    private final ILogEncoder logEncoder;
    private final ILogSender logSender;
    private final LabelSettings labelSettings;
    private final IExecutor executor;
    private final IBuffering bufferingManager;
    private final ILogMonitor logMonitor;

    /**
     * Main constructor designed for user of this library.
     *
     * @param logCollector                    ILogCollector implementation, which is responsible for creating new streams and collecting its logs.
     * @param logEncoder                      Optional (nullable) log encoder which is responsible for encode whole log message.
     * @param logSenderSettings               {@link LogSenderSettings} used to initialize the {@link ILogSender log sender}.
     *                                        Some settings will be overridden by this constructor.
     * @param logSender                       Sends the logs collected by log controller.
     * @param labelSettings                   Preferences of the {@link Labels}. See {@link LabelSettings}.
     * @param structuredMetadataLabelSettings Structured metadata label restrictions. If <c>null</c>, the structured metadata is not supported.
     * @param executor                        The {@link IExecutor} implementation.
     * @param bufferingManager                The object responsible for buffering strategy.
     * @param logMonitor                      Handles diagnostic events from whole library.
     * @since 0.3.4
     */
    public TinyLoki(final ILogCollector logCollector, ILogEncoder logEncoder, final LogSenderSettings logSenderSettings, final ILogSender logSender, final LabelSettings labelSettings, final LabelSettings structuredMetadataLabelSettings, final IExecutor executor, final IBuffering bufferingManager, final ILogMonitor logMonitor) {
        this.logCollector = logCollector;
        this.logEncoder = logEncoder;
        this.logSender = logSender;
        this.labelSettings = labelSettings;
        this.executor = executor;
        this.bufferingManager = bufferingManager;
        this.logMonitor = logMonitor;

        this.bufferingManager.configure(this.logCollector, 0, 0, this.executor, this.logMonitor);
        this.logCollector.configureBufferingManager(this.bufferingManager);
        this.logCollector.configureStructuredMetadata(structuredMetadataLabelSettings);
        logSenderSettings.setContentType(this.logCollector.contentType());
        final String contentEncoding = (this.logEncoder == null) ? null : this.logEncoder.contentEncoding();
        logSenderSettings.setContentEncoding(contentEncoding);
        this.logSender.configure(logSenderSettings, logMonitor);
        this.executor.configure(logCollector, this::internalProcessLogs, logMonitor);
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
     * @deprecated Use {@link TinyLoki#TinyLoki(ILogCollector, ILogEncoder, LogSenderSettings, ILogSender, LabelSettings, LabelSettings, IExecutor, IBuffering, ILogMonitor)} instead, where logEncoder parameter should be specified explicitly.
     */
    public TinyLoki(final ILogCollector logCollector, final LogSenderSettings logSenderSettings, final ILogSender logSender, final LabelSettings labelSettings, final IExecutor executor, final ILogMonitor logMonitor) {
        this(logCollector, null, logSenderSettings, logSender, labelSettings, new LabelSettings(), executor, new BasicBuffering(), logMonitor);
    }

    /**
     * THe log monitor getter.
     *
     * @return The log monitor set in the constructor.
     * @since 1.0.0
     */
    public ILogMonitor getLogMonitor() {
        return this.logMonitor;
    }

    /**
     * Provides the {@link ILogCollector} instance used to initialize this <code>LogController</code>.
     *
     * @return The {@link ILogCollector} instance used to initialize this <code>LogController</code>.
     * @since 1.0.0
     */
    public ILogCollector getLogCollector() {
        return logCollector;
    }

    /**
     * Provides the {@link IExecutor} instance used to initialize this <code>LogController</code>.
     *
     * @return The {@link IExecutor} instance used to initialize this <code>LogController</code>.
     * @since 1.0.0
     */
    public IExecutor getExecutor() {
        return executor;
    }

    /**
     * Creates new stream from log collector.
     *
     * @param labels Static labels. There shouldn't be many streams with the same labels combination.
     * @return New stream reference.
     */
    @SuppressWarnings("unused")
    public ILogStream createStream(final Map<String, String> labels) {
        return this.createStream(Labels.of(labels));
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
     * <p>
     * <b>Thread safety</b>
     * <p>
     * It may be called from any thread.
     *
     * @return This reference.
     * @throws RuntimeException If executor already started and execution thread/environment was already initialize.
     */
    public TinyLoki start() {
        try {
            executor.start();
            logMonitor.onStart();
        } catch (Exception e) {
            logMonitor.onException(e);
            throw e;
        }
        return this;
    }

    /**
     * Blocking function. Stops the executor sending the logs in the background.
     * <p>
     * <b>Thread safety</b>
     * <p>
     * It may be called from any thread.
     *
     * @param timeout Maximum blocking time. The <code>0</code> or a negative value will result in the operation not being performed and <code>false</code> will be returned.
     * @return <code>true</code> If execution has stopped successfully.
     * <p>
     * <code>false</code> If execution has not stopped due to timeout or invalid <code>timeout</code> argument.
     * @throws InterruptedException When calling thread is interrupted so cannot wait for executor's thread.
     * @since 1.0.0
     */
    public boolean stop(final int timeout) throws InterruptedException {
        try {
            if (timeout <= 0) {
                throw new IllegalArgumentException("Cannot stop with timeout <= 0: [" + timeout + "].");
            }
            final boolean result = executor.stop(timeout);
            logMonitor.onStop(result);
            return result;
        } catch (final InterruptedException e) {
            throw e;
        } catch (final Exception e2) {
            logMonitor.onException(e2);
            return false;
        }
    }

    /**
     * Blocking function. Stops the executor sending the logs in the background with default timeout defined as {@link #DEFAULT_STOP_TIMEOUT}.
     * <p>
     * <b>Thread safety</b>
     * <p>
     * It may be called from any thread.
     *
     * @return <code>true</code> If execution has stopped successfully.
     * <p>
     * <code>false</code> If execution has not stopped due to timeout.
     * @throws InterruptedException When calling thread is interrupted.
     * @since 1.0.0
     */
    public boolean stop() throws InterruptedException {
        return this.stop(DEFAULT_STOP_TIMEOUT);
    }

    /**
     * Blocking function. Blocks the calling thread until all requested logs are processed (attempted to send) or timeout occurs.
     * <p>
     * <b>Thread safety</b>
     * <p>
     * It may be called from any thread.
     *
     * @param timeout The maximum time in milliseconds of blocking the calling thread.
     *                The <code>0</code> or negative value will cause returning immediately.
     * @return <code>true</code> If sync operation finished with success.
     * <p>
     * <code>false</code> If sync operation failed due to timeout or given <code>timeout</code> is less or equal <code>0</code>.
     * @throws InterruptedException When calling thread is interrupted.
     * @since 1.0.0
     */
    public boolean sync(final int timeout) throws InterruptedException {
        try {
            if (timeout <= 0) {
                throw new IllegalArgumentException("Cannot sync with timeout <= 0: [" + timeout + "].");
            }
            final boolean result = executor.sync(timeout);
            logMonitor.onSync(result);
            return result;
        } catch (final InterruptedException e) {
            throw e;
        } catch (final Exception e2) {
            logMonitor.onException(e2);
            return false;
        }
    }

    /**
     * Blocking function. Blocks the calling thread until all requested logs are processed (attempted to send)
     * or default timeout occurs, defined as {@link #DEFAULT_SYNC_TIMEOUT}.
     * <p>
     * <b>Thread safety</b>
     * <p>
     * It may be called from any thread.
     *
     * @return <code>true</code> If sync operation finished, even if the HTTP operation has failed due to IO errors.
     * <p>
     * <code>false</code> If sync operation failed due to timeout.
     * @throws InterruptedException When calling thread is interrupted.
     * @since 1.0.0
     */
    public boolean sync() throws InterruptedException {
        return sync(DEFAULT_SYNC_TIMEOUT);
    }

    /**
     * Blocking function. Tries to call {@link #sync(int)} and then {@link #stop(int)} (even if <code>sync</code> fails).
     * <p>
     * <b>Thread safety</b>
     * <p>
     * It may be called from any thread.
     *
     * @param syncTimeout The maximum time to <code>sync</code> in milliseconds.
     * @param stopTimeout The maximum time to wait for executor stopping in milliseconds.
     *                    The waiting time is reduced to 10 milliseconds if <code>sync</code> has been broken by thread interrupted exception.
     * @return <code>true</code> If synchronization and stopping passed with success.
     * <p>
     * <code>false</code> On synchronization or stopping timeout.
     * @throws InterruptedException When calling thread is interrupted.
     * @since 1.0.0
     */
    public boolean closeSync(final int syncTimeout, final int stopTimeout) throws InterruptedException {
        InterruptedException syncInterruptedException = null;
        int finalStopTimeout = stopTimeout;
        boolean syncSuccess = false;
        boolean stopSuccess = false;

        try {
            syncSuccess = this.sync(syncTimeout);
        } catch (final InterruptedException e) {
            syncInterruptedException = e;
            finalStopTimeout = Math.min(stopTimeout, 10);
        }

        stopSuccess = this.stop(finalStopTimeout);

        if (syncInterruptedException != null) {
            final InterruptedException finalException = new InterruptedException("The closeSync() has been interrupted during sync. Trying to stop with result: " + stopSuccess);
            finalException.initCause(syncInterruptedException);
            throw finalException;
        }

        return syncSuccess && stopSuccess;
    }

    /**
     * Blocking function. Tries to call {@link #sync(int)} and then {@link #stop(int)} even if <code>sync</code> fails.
     *
     * @param timeout The maximum time for synchronizing and stopping at all. Half of time is for syncing and half for stopping.
     * @return <code>true</code> If synchronization and stopping passed with success.
     * <p>
     * <code>false</code> On synchronization or stopping timeout.
     * @throws InterruptedException When calling thread is interrupted.
     */
    public boolean closeSync(final int timeout) throws InterruptedException {
        return closeSync(timeout / 2, timeout / 2);
    }

    /**
     * Blocking function. Tries to call {@link #sync()} and then {@link #stop()} even if <code>sync</code> fails.
     * <p>
     * Default timeout values are used.
     *
     * @return <code>true</code> If synchronization and stopping passed with success.
     * <p>
     * <code>false</code> On synchronization or stopping timeout.
     * @throws InterruptedException When calling thread is interrupted.
     * @see #DEFAULT_SYNC_TIMEOUT
     * @see #DEFAULT_STOP_TIMEOUT
     */
    public boolean closeSync() throws InterruptedException {
        return closeSync(DEFAULT_SYNC_TIMEOUT, DEFAULT_STOP_TIMEOUT);
    }

    /**
     * Used by the {@link IExecutor} to process logs in execution context (separate or borrowed thread, task, etc.).
     * Should <b style="color:red">not</b> be called by the library client directly.
     *
     * @throws InterruptedException When calling thread is interrupted.
     * @throws RuntimeException     When encoding error or other error occurs.
     * @since 1.0.0
     */
    protected void internalProcessLogs() throws InterruptedException {
        final byte[][] buffers = logCollector.collectAll();
        if (buffers == null) {
            logMonitor.logInfo("Buffers count: [0] (null).");
            return;
        }

        logMonitor.logInfo("Buffers count: [" + buffers.length + "].");
        for (final byte[] logs : buffers) {
            final byte[] toSend;
            if (logEncoder == null) {
                toSend = logs;
            } else {
                try {
                    toSend = logEncoder.encode(logs);
                } catch (final IOException e) {
                    throw new RuntimeException("Failed to encode logs.", e);
                }
                logMonitor.onEncoded(logs, toSend);
            }

            try {
                logSender.send(toSend);
            } catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Requests the executor (worker thread) to stop ASAP.
     * <p>
     * Buffered but not sent logs are dropped.
     * <p>
     * <b>Thread safety</b><br>
     * It may be called from any thread.
     *
     * @return This reference.
     * @since 1.0.0
     */
    public TinyLoki stopAsync() {
        executor.stopAsync();
        return this;
    }

    /**
     * Interrupts worker thread and tries to join for given interruptTimeout.
     *
     * @param interruptTimeout Timeout in milliseconds. The value is clamped to <code>int</code> range.
     * @return This reference.
     * @see #stop()
     * @see #stop(int)
     * @deprecated Since <code>1.0.0</code>. Use {@link #stop(int)} or {@link #closeSync(int, int)} instead.
     */
    @Deprecated
    public TinyLoki hardStop(final long interruptTimeout) {
        try {
            this.stop(Utils.clampToInt(interruptTimeout));
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
     * @see #stop(int)
     * @see #stop()
     * @deprecated Since <code>1.0.0</code>. Use {@link #stop(int)} or {@link #closeSync(int, int)} instead.
     */
    @Deprecated
    @SuppressWarnings("UnusedReturnValue")
    public TinyLoki hardStop() {
        return this.hardStop(DEFAULT_HARD_STOP_WAIT_TIME);
    }

    /**
     * Blocking function. Tells to worker to send logs to this time point and exit.
     *
     * @param softTimeout Timeout in milliseconds. The value is clamped to <code>int</code> range.
     * @return This reference.
     * @see #closeSync(int, int)
     * @deprecated Since <code>1.0.0</code>. Use {@link #closeSync(int, int)} instead.
     */
    @Deprecated
    public TinyLoki softStop(final long softTimeout) {
        try {
            final int normalizedTimeout = Utils.clampToInt(softTimeout);
            this.closeSync(normalizedTimeout);
        } catch (final Exception e) {
            logMonitor.onException(e);
        }
        return this;
    }

    /**
     * Blocking function. Soft stopAnd with default timeout.
     *
     * @return This reference.
     * @see #softStop(long)
     * @see #closeSync(int, int)
     * @deprecated Since <code>1.0.0</code>. Use {@link #closeSync(int, int)} instead.
     */
    @Deprecated
    @SuppressWarnings("unused")
    public TinyLoki softStop() {
        return this.softStop(DEFAULT_SOFT_STOP_WAIT_TIME);
    }

    /**
     * Tells if worker thread has stopped softly, doing all its work before exiting.
     *
     * @return Always <code>true</code>.
     * @deprecated Since <code>1.0.0</code>. This method will be removed.
     */
    @Deprecated
    public boolean isSoftStopped() {
        return true;
    }

    /**
     * Requests the executor (worker thread) to stop ASAP. Not synchronized logs are dropped.
     *
     * @since 1.0.0
     */
    @Override
    public void close() {
        this.stopAsync();
    }

    /**
     * Requests the executor (worker thread) to stop ASAP. Not synchronized logs are dropped.
     *
     * @since 1.0.0
     */
    @Override
    protected void finalize() {
        this.stopAsync();
    }
}
