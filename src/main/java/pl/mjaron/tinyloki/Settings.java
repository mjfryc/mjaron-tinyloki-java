package pl.mjaron.tinyloki;

/**
 * Configuration used to create a new {@link LogController} object.
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
     * @since 0.4.0
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
     * @see #fromArbitraryUrl(String)
     * @since 0.4.0
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
     * @since 0.4.0
     */
    public static Settings fromArbitraryUrl(final String url) {
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
     * @since 0.4.0
     */
    private IExecutor executor = null;

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
     * Sets the {@link IExecutor} used to perform background operations like log collecting and sendint.
     *
     * @param executor The {@link IExecutor} instance. The <code>null</code> value causes using the default executor.
     * @return This {@link Settings} object reference.
     * @since 0.4.0
     */
    public Settings withExecutor(final IExecutor executor) {
        this.executor = executor;
        return this;
    }

    /**
     * Allows setting the {@link IBuffering} implementation.
     *
     * @param buffering The {@link IBuffering} implementation.
     * @return This {@link Settings} object reference.
     * @since 0.4.0
     */
    public Settings withBuffering(final IBuffering buffering) {
        this.buffering = buffering;
        return this;
    }

    /**
     * Allows to set and configure the {@link BasicBuffering}.
     *
     * @param maxMessageSize  The max single message size.
     *                        This size should be lower than the Grafana Loki server max message size.
     *                        The default value in this library: {@link IBuffering#DEFAULT_MAX_MESSAGE_SIZE}.
     * @param maxBuffersCount The max buffers count. The value should be tuned to target system capabilities.
     *                        The default value in this library: {@link IBuffering#DEFAULT_MAX_BUFFERS_COUNT}.
     * @return This {@link Settings} object reference.
     * @since 0.4.0
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
     * @since 0.4.0
     */
    public IBuffering getBuffering() {
        if (buffering == null) {
            buffering = new BasicBuffering();
        }
        return buffering;
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
     * Provides the {@link IExecutor} instance defined by this library's user or default executor if user hasn't specified the executor.
     *
     * @return Selected {@link IExecutor} instance.
     * @since 0.4.0
     */
    public IExecutor getExecutor() {
        if (executor == null) {
            executor = new ThreadExecutor();
        }
        return executor;
    }

    /**
     * Creates and starts the {@link LogController} instance using parameters defined previously by this settings object.
     *
     * @return New {@link LogController} instance.
     */
    public LogController start() {
        return TinyLoki.createAndStart(this);
    }
}
