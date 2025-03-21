package pl.mjaron.tinyloki;

/**
 * Configuration used to create a new {@link TinyLoki} object.
 *
 * @since 0.3.0
 */
public class Settings {

    /**
     * Tries to resolve the final URL used to connect to the Grafana Loki server.
     * <p>
     *
     * <strong>Valid URL formats</strong>
     * <p>
     * E.g:
     * <pre> {@code
     * http://localhost:3100/loki/api/v1/push/
     * http://localhost:3100/loki/api/v1/push
     * http://localhost:3100/
     * http://localhost:3100
     * } </pre>
     * <p>
     * will be converted to value:
     * <pre>{@code
     * http://localhost:3100/loki/api/v1/push
     * }</pre>
     *
     * @param url The server URL. The logs pushing endpoint will be automatically added if missing.
     * @return Normalized URL.
     * @since 1.0.0
     */
    public static String normalizeUrl(final String url) {
        if (url.endsWith(TinyLoki.API_PUSH)) {
            return url;
        }

        if (url.endsWith(TinyLoki.API_PUSH + "/")) {
            return url.substring(0, url.length() - 1);
        }

        if (!url.endsWith("/")) {
            return url + "/" + TinyLoki.API_PUSH;
        }

        return url + TinyLoki.API_PUSH;
    }

    /**
     * The recommended method to create {@link Settings} instance.
     * Creates settings from given URL which is {@link #normalizeUrl(String) normalized} before using.
     *
     * @param url Any value used as URL when connecting to the Grafana Loki server.
     *            <p>
     *            See {@link #normalizeUrl(String)} for more details about accepted URL formats.
     * @return New {@link Settings} instance.
     * @see #normalizeUrl(String)
     * @see #fromExactUrl(String)
     * @since 1.0.0
     */
    public static Settings fromUrl(final String url) {
        return new Settings(normalizeUrl(url));
    }

    /**
     * Creates settings based on any URL value, even if the URL is completely invalid.
     * <p>
     * Doesn't verify if the {@link TinyLoki#API_PUSH Grafana Loki API endpoint} is added.
     *
     * @param url Any value used as URL when connecting to the Grafana Loki server.
     * @return New {@link Settings} instance.
     * @see #fromUrl(String)
     * @since 1.0.0
     */
    public static Settings fromExactUrl(final String url) {
        return new Settings(url);
    }

    /**
     * {@link LogSenderSettings} instance.
     *
     * @since 0.3.0
     */
    private final LogSenderSettings logSenderSettings = new LogSenderSettings();
    /**
     * Parameters of valid label values.
     *
     * @see LabelSettings
     * @see LabelSettings#DEFAULT_MAX_LABEL_NAME_LENGTH
     * @see LabelSettings#DEFAULT_MAX_LABEL_VALUE_LENGTH
     * @see Settings#withLabelLength(int, int)
     */
    private final LabelSettings labelSettings = new LabelSettings();

    /**
     * If not null, the structured metadata is supported, otherwise, it will be ignored in log streams.
     *
     * @since 1.1.0
     */
    private LabelSettings structuredMetadataLabelSettings = new LabelSettings();
    /**
     * {@link ILogCollector} which will prepare logs to send it to the Grafana Loki server.
     *
     * @since 0.3.0
     */
    private ILogCollector logCollector = null;
    /**
     * {@link ILogEncoder} which is responsible for encode whole log message.
     *
     * @since 0.3.4
     */
    private ILogEncoder logEncoder = null;

    private IBuffering buffering = null;

    /**
     * {@link ILogMonitor} which is used for this library diagnostic and error handling.
     *
     * @since 0.3.0
     */
    private ILogMonitor logMonitor = null;
    /**
     * {@link ILogSender} which is used to send logs.
     *
     * @since 0.3.0
     */
    private ILogSender logSender = null;
    /**
     * The {@link IExecutor} used to perform background log collecting and sending operations.
     *
     * @since 1.0.0
     */
    private IExecutor executor = null;

    private ITimestampProviderFactory timestampProviderFactory = null;

    /**
     * Settings constructor with initial URL value.
     *
     * @param url URL to Loki HTTP API endpoint, usually ending with <code>/loki/api/v1/push</code>.
     * @since 0.3.0
     */
    private Settings(final String url) {
        logSenderSettings.setUrl(url);
    }

    /**
     * Sets the HTTP Basic Auth credentials.
     *
     * @param user Username.
     * @param pass Password.
     * @return This {@link Settings} object reference.
     * @since 0.3.0
     */
    public Settings withBasicAuth(final String user, final String pass) {
        logSenderSettings.setUser(user);
        logSenderSettings.setPassword(pass);
        return this;
    }

    /**
     * Sets the timeout of HTTP connection used to send logs to the Grafana Loki server.
     * <p>
     * Overwrites the {@link LogSenderSettings#DEFAULT_CONNECT_TIMEOUT default connect timeout} value.
     *
     * @param connectTimeout Time in milliseconds.
     * @return This {@link Settings} object reference.
     * @since 0.3.0
     */
    public Settings withConnectTimeout(final int connectTimeout) {
        logSenderSettings.setConnectTimeout(connectTimeout);
        return this;
    }

    /**
     * Allows changing the default {@link ILogCollector}, which currently is {@link JsonLogCollector}.
     *
     * @param logCollector Instance of custom {@link ILogCollector}.
     * @return This {@link Settings} object reference.
     * @since 0.3.0
     */
    public Settings withLogCollector(final ILogCollector logCollector) {
        this.logCollector = logCollector;
        return this;
    }

    /**
     * Allows changing the default {@link ILogEncoder}, which currently is null (no log encoder).
     *
     * @param logEncoder Instance of custom {@link ILogEncoder}.
     * @return This {@link Settings} object reference.
     * @since 0.3.4
     */
    public Settings withLogEncoder(final ILogEncoder logEncoder) {
        this.logEncoder = logEncoder;
        return this;
    }

    /**
     * Sets the {@link ILogEncoder} used to encode logs to {@link GzipLogEncoder}.
     *
     * @return This {@link Settings} object reference.
     * @since 0.3.4
     */
    public Settings withGzipLogEncoder() {
        return this.withLogEncoder(new GzipLogEncoder());
    }

    /**
     * Resets the {@link ILogEncoder} to no encoder, so logs will not be encoded before sending.
     *
     * @return This {@link Settings} object reference.
     * @since 0.3.4
     */
    public Settings withoutLogEncoder() {
        return this.withLogEncoder(null);
    }

    /**
     * Allows changing the default {@link ILogMonitor}, which currently is {@link ErrorLogMonitor}.
     *
     * @param logMonitor Instance of custom {@link ILogMonitor}.
     * @return This {@link Settings} object reference.
     * @since 0.3.0
     */
    public Settings withLogMonitor(final ILogMonitor logMonitor) {
        this.logMonitor = logMonitor;
        return this;
    }

    /**
     * Allows setting {@link ErrorLogMonitor} explicitly.
     *
     * @return This {@link Settings} object reference.
     * @since 0.3.4
     */
    public Settings withErrorLogMonitor() {
        return this.withLogMonitor(new ErrorLogMonitor());
    }

    /**
     * Allows setting {@link VerboseLogMonitor} explicitly.
     *
     * @return This {@link Settings} object reference.
     * @since 0.3.4
     */
    public Settings withVerboseLogMonitor() {
        return this.withLogMonitor(new VerboseLogMonitor());
    }

    /**
     * Allows setting {@link VerboseLogMonitor} explicitly and set if messages also should be printed.
     *
     * @param printMessages Tells whether sent messages content should be also printed.
     * @return This {@link Settings} object reference.
     * @since 0.3.4
     */
    public Settings withVerboseLogMonitor(final boolean printMessages) {
        return this.withLogMonitor(new VerboseLogMonitor(printMessages));
    }

    /**
     * Allows changing the default {@link ILogSender}, which currently is {@link HttpLogSender}.
     *
     * @param logSender Instance of custom {@link ILogSender}.
     * @return This {@link Settings} object reference.
     * @since 0.3.0
     */
    public Settings withLogSender(final ILogSender logSender) {
        this.logSender = logSender;
        return this;
    }

    /**
     * Allows changing the default limits of length of label name and value.
     *
     * @param maxLabelNameLength  Custom value of label name length. Must be positive.
     * @param maxLabelValueLength Custom value of label value length. Must be positive.
     * @return This {@link Settings} object reference.
     * @see LabelSettings
     * @see LabelSettings#DEFAULT_MAX_LABEL_NAME_LENGTH
     * @see LabelSettings#DEFAULT_MAX_LABEL_VALUE_LENGTH
     * @see Labels
     * @see Labels#prettify(Labels, int, int)
     * @since 0.3.0
     */
    public Settings withLabelLength(final int maxLabelNameLength, final int maxLabelValueLength) {
        this.labelSettings.setMaxLabelNameLength(maxLabelNameLength);
        this.labelSettings.setMaxLabelValueLength(maxLabelValueLength);
        return this;
    }

    /**
     * Enables structured metadata support.
     *
     * @param structuredMetadataLabelSettings Limits of structured metadata identifiers.
     * @return This {@link Settings} object reference.
     * @since 1.1.0
     */
    public Settings withStructuredMetadata(final LabelSettings structuredMetadataLabelSettings) {
        this.structuredMetadataLabelSettings = structuredMetadataLabelSettings;
        return this;
    }

    /**
     * Enables structured metadata support with default structured metadata label settings.
     *
     * @return This {@link Settings} object reference.
     * @since 1.1.0
     */
    public Settings withStructuredMetadata() {
        return withStructuredMetadata(new LabelSettings());
    }

    /**
     * Disables structured metadata support. It will be not sent to the server.
     *
     * @return This {@link Settings} object reference.
     * @since 1.1.0
     */
    public Settings withoutStructuredMetadata() {
        this.structuredMetadataLabelSettings = null;
        return this;
    }

    /**
     * Sets the {@link IExecutor} used to perform background operations like log collecting and sendint.
     *
     * @param executor The {@link IExecutor} instance. The <code>null</code> value causes using the default executor.
     * @return This {@link Settings} object reference.
     * @since 1.0.0
     */
    public Settings withExecutor(final IExecutor executor) {
        this.executor = executor;
        return this;
    }

    /**
     * Sets the new instance of {@link ThreadExecutor} with custom interval time between sending logs.
     * <p>
     * The default log processing interval time is defined by {@link ThreadExecutor#DEFAULT_PROCESSING_INTERVAL_TIME_MS}.
     *
     * @param processingIntervalTime Interval time between sending logs in milliseconds.
     * @return This {@link Settings} object reference.
     * @see ThreadExecutor#DEFAULT_PROCESSING_INTERVAL_TIME_MS
     * @since 1.0.0
     */
    public Settings withThreadExecutor(final int processingIntervalTime) {
        return withExecutor(new ThreadExecutor(processingIntervalTime));
    }

    /**
     * Sets the new instance of {@link ThreadExecutor} with default interval time between sending logs.
     * <p>
     * The default log processing interval time is defined by {@link ThreadExecutor#DEFAULT_PROCESSING_INTERVAL_TIME_MS}.
     * <p>
     * The {@link ThreadExecutor} is the default executor if no other executor is set.
     *
     * @return This {@link Settings} object reference.
     * @see ThreadExecutor#DEFAULT_PROCESSING_INTERVAL_TIME_MS
     * @since 1.0.0
     */
    public Settings withThreadExecutor() {
        return withExecutor(new ThreadExecutor());
    }

    /**
     * Allows setting the {@link IBuffering} implementation.
     *
     * @param buffering The {@link IBuffering} implementation.
     * @return This {@link Settings} object reference.
     * @since 1.0.0
     */
    public Settings withBuffering(final IBuffering buffering) {
        this.buffering = buffering;
        return this;
    }

    /**
     * Determines the log timestamp value policy. See {@link ITimestampProvider} for details.
     *
     * @param timestampProviderFactory The factory creating {@link ITimestampProvider}.
     * @return This {@link Settings} object reference.
     * @see ITimestampProvider
     * @since 1.1.3
     */
    public Settings withTimestampProvider(final ITimestampProviderFactory timestampProviderFactory) {
        this.timestampProviderFactory = timestampProviderFactory;
        return this;
    }

    /**
     * Sets the {@link CurrentTimestampProvider}. See {@link ITimestampProvider} for details.
     * <p>
     * If {@link ITimestampProviderFactory} is not set, the {@link ITimestampProviderFactory#getDefault()} value will be used.
     *
     * @return This {@link Settings} object reference.
     * @since 1.1.3
     */
    public Settings withCurrentTimestampProvider() {
        return withTimestampProvider(ITimestampProviderFactory.current());
    }

    /**
     * Sets the {@link IncrementingTimestampProvider}. See {@link ITimestampProvider} for details.
     * <p>
     * If {@link ITimestampProviderFactory} is not set, the {@link ITimestampProviderFactory#getDefault()} value will be used.
     *
     * @return This {@link Settings} object reference.
     * @since 1.1.3
     */
    public Settings withIncrementingTimestampProvider() {
        return withTimestampProvider(ITimestampProviderFactory.incrementing());
    }

    /**
     * Allows to set and configure the {@link BasicBuffering}.
     *
     * @param maxMessageSize  The max single (not encoded) message size.
     *                        This size should be lower than the Grafana Loki server max message size.
     *                        <p>
     *                        The default value in this library: {@link IBuffering#DEFAULT_MAX_MESSAGE_SIZE}.
     *                        <p>
     *                        Note that if this size is exceeded, the server may respond with HTTP error <code>500</code>, e.g:
     *                        <pre>{@code rpc error: code = ResourceExhausted desc = grpc: received message larger than max (6331143 vs. 4194304)}</pre>
     * @param maxBuffersCount The max buffers count. The value should be tuned to target system capabilities.
     *                        <p>
     *                        The default value in this library: {@link IBuffering#DEFAULT_MAX_BUFFERS_COUNT}.
     * @return This {@link Settings} object reference.
     * @since 1.0.0
     */
    public Settings withBasicBuffering(final int maxMessageSize, final int maxBuffersCount) {
        this.buffering = new BasicBuffering(maxMessageSize, maxBuffersCount);
        return this;
    }

    /**
     * Getter of {@link LogSenderSettings}. Used by TinyLoki to initialize the {@link HttpLogSender}.
     *
     * @return Reference to {@link LogSenderSettings} instance in this settings object.
     * @since 0.3.0
     */
    public LogSenderSettings getLogSenderSettings() {
        return logSenderSettings;
    }

    /**
     * Getter of {@link ILogCollector}. Used by TinyLoki to obtain selected log collector.
     *
     * @return Selected {@link ILogCollector log collector}.
     * @since 0.3.0
     */
    public ILogCollector getLogCollector() {
        return (logCollector != null) ? logCollector : new JsonLogCollector();
    }

    /**
     * Getter of {@link ILogEncoder}.
     *
     * @return Selected {@link ILogEncoder} or null if there is no encoder.
     * @since 0.3.4
     */
    public ILogEncoder getLogEncoder() {
        return logEncoder;
    }

    /**
     * Getter of {@link IBuffering}.
     *
     * @return Selected {@link IBuffering} or default value if not set.
     * @since 1.0.0
     */
    public IBuffering getBuffering() {
        if (buffering == null) {
            buffering = new BasicBuffering();
        }
        return buffering;
    }

    /**
     * Getter of {@link ITimestampProviderFactory}. See {@link ITimestampProvider} for details.
     *
     * @return Selected {@link ITimestampProviderFactory} or default value if not set.
     * @since 1.1.3
     */
    public ITimestampProviderFactory getTimestampProviderFactory() {
        if (timestampProviderFactory == null) {
            timestampProviderFactory = ITimestampProviderFactory.getDefault();
        }
        return timestampProviderFactory;
    }

    /**
     * Getter of {@link ILogMonitor}. Used by TinyLoki to obtain selected log monitor.
     *
     * @return Selected {@link ILogMonitor log monitor}.
     * @since 0.3.0
     */
    public ILogMonitor getLogMonitor() {
        if (logMonitor == null) {
            logMonitor = new ErrorLogMonitor();
        }
        return logMonitor;
    }

    /**
     * Getter of {@link ILogSender}. Used by TinyLoki to obtain selected log sender.
     *
     * @return Selected {@link ILogSender log sender}.
     * @since 0.3.0
     */
    public ILogSender getLogSender() {
        if (logSender == null) {
            logSender = new HttpLogSender();
        }
        return logSender;
    }

    /**
     * Gives {@link LabelSettings} defined by this library's user.
     *
     * @return Selected {@link LabelSettings}.
     */
    public LabelSettings getLabelSettings() {
        return this.labelSettings;
    }

    /**
     * Provides {@link LabelSettings} of structured metadata labels. If <code>null</code>, the structured metadata is not supported.
     *
     * @return {@link LabelSettings} of structured metadata labels. If <code>null</code>, the structured metadata is not supported.
     * @since 1.1.0
     */
    public LabelSettings getStructuredMetadataLabelSettings() {
        return this.structuredMetadataLabelSettings;
    }

    /**
     * Provides the {@link IExecutor} instance defined by this library's user or default executor if user hasn't specified the executor.
     *
     * @return Selected {@link IExecutor} instance.
     * @since 1.0.0
     */
    public IExecutor getExecutor() {
        if (executor == null) {
            executor = new ThreadExecutor();
        }
        return executor;
    }

    /**
     * Creates and starts the {@link TinyLoki} instance using parameters defined previously by this settings object.
     *
     * @return New {@link TinyLoki} instance.
     */
    public TinyLoki open() {
        return TinyLoki.open(this);
    }
}
