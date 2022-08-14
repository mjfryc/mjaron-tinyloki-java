package pl.mjaron.tinyloki;

import java.util.Map;

/**
 * Top-level class which allows initializing the logging system.
 */
@SuppressWarnings("unused")
public class TinyLoki {

    /**
     * Configuration used to create a new {@link LogController} object.
     *
     * @since 0.3.0
     */
    public static class Settings {

        /**
         * {@link LogSenderSettings} instance.
         *
         * @since 0.3.0
         */
        private final LogSenderSettings logSenderSettings = LogSenderSettings.create();

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
         * Parameters of valid label values.
         *
         * @see LabelSettings
         * @see LabelSettings#DEFAULT_MAX_LABEL_NAME_LENGTH
         * @see LabelSettings#DEFAULT_MAX_LABEL_VALUE_LENGTH
         * @see Settings#withLabelLength(int, int)
         */
        private final LabelSettings labelSettings = new LabelSettings();

        /**
         * Settings constructor with initial URL value.
         *
         * @param url URL to Loki HTTP API endpoint, usually ending with <code>/loki/api/v1/push</code>.
         * @since 0.3.0
         */
        public Settings(final String url) {
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
         * Getter of {@link ILogMonitor}. Used by TinyLoki to obtain selected log monitor.
         *
         * @return Selected {@link ILogMonitor log monitor}.
         * @since 0.3.0
         */
        public ILogMonitor getLogMonitor() {
            return (logMonitor != null) ? logMonitor : new ErrorLogMonitor();
        }

        /**
         * Getter of {@link ILogSender}. Used by TinyLoki to obtain selected log sender.
         *
         * @return Selected {@link ILogSender log sender}.
         * @since 0.3.0
         */
        public ILogSender getLogSender() {
            return (logSender != null) ? logSender : new HttpLogSender();
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
         * Creates and starts the {@link LogController} instance using parameters defined previously by this settings object.
         *
         * @return New {@link LogController} instance.
         */
        public LogController start() {
            return TinyLoki.createAndStart(this);
        }
    }

    /**
     * Creates a new {@link Settings} object which is used to initialize a new {@link LogController} instance, e.g:
     * <pre>
     *     LogController logController =
     *         TinyLoki.withUrl(url)    // Settings created here.
     *             .withBasicAuth(user, pass)
     *             .withConnectTimeout(connectTimeout)
     *             .withLogCollector(logCollector)
     *             .withLogMonitor(logMonitor)
     *             .start();            // LogController created here.
     * </pre>
     *
     * @param url URL to Loki HTTP API endpoint, usually ending with <code>/loki/api/v1/push</code>.
     * @return New instance of {@link Settings} object, initialized with given <code>url</code>.
     */
    public static Settings withUrl(final String url) {
        return new Settings(url);
    }

    /**
     * Creates a new {@link LogController} with given {@link Settings}.
     *
     * @param settings Configuration of a newly created {@link LogController}.
     * @return New instance of {@link LogController}.
     * @since 0.3.0
     */
    public static LogController createAndStart(final Settings settings) {
        return new LogController(settings.getLogCollector(), settings.getLogEncoder(), settings.getLogSenderSettings(), settings.getLogSender(), settings.getLabelSettings(), settings.getLogMonitor()).start();
    }

    /**
     * Creates a basic configuration of LogController.
     *
     * @param url  URL to Loki HTTP API endpoint, usually ending with <code>/loki/api/v1/push</code>.
     * @param user Basic authentication user. If null, BA header will not be sent.
     * @param pass Basic authentication password. If null, BA header will not be sent.
     * @return New {@link pl.mjaron.tinyloki.LogController LogController} object.
     * @deprecated Use {@link TinyLoki#withUrl(String)} to initialize settings and finally call {@link Settings#start()}, e.g:
     * <pre>
     *     TinyLoki.withUrl(url).withBasicAuth(user, pass).start();
     * </pre>
     */
    public static LogController createAndStart(final String url, final String user, final String pass) {
        return TinyLoki.withUrl(url).withBasicAuth(user, pass).start();
    }

    /**
     * Creates a basic configuration of LogController.
     *
     * @param url            URL to Loki HTTP API endpoint, usually ending with <code>/loki/api/v1/push</code>.
     * @param user           Basic authentication user. If null, BA header will not be sent.
     * @param pass           Basic authentication password. If null, BA header will not be sent.
     * @param connectTimeout HTTP log server connection timeout in milliseconds.
     * @return New {@link pl.mjaron.tinyloki.LogController LogController} object.
     * @deprecated Use {@link TinyLoki#withUrl(String)} to initialize settings and finally call {@link Settings#start()}, e.g:
     * <pre>
     *     TinyLoki.withUrl(url)
     *         .withBasicAuth(user, pass)
     *         .withConnectTimeout(connectTimeout)
     *         .start();
     * </pre>
     */
    public static LogController createAndStart(final String url, final String user, final String pass, final int connectTimeout) {
        return TinyLoki.withUrl(url).withBasicAuth(user, pass).withConnectTimeout(connectTimeout).start();
    }

    /**
     * Creates a configuration of LogController.
     *
     * @param url          URL to Loki HTTP API endpoint, usually ending with <code>/loki/api/v1/push</code>.
     * @param user         Basic authentication user. If null, BA header will not be sent.
     * @param pass         Basic authentication password. If null, BA header will not be sent.
     * @param logCollector {@link pl.mjaron.tinyloki.ILogCollector ILogCollector} instance.
     * @param logMonitor   {@link pl.mjaron.tinyloki.ILogMonitor ILogMonitor} instance.
     * @return New {@link pl.mjaron.tinyloki.LogController LogController} object.
     * @deprecated Use {@link TinyLoki#withUrl(String)} to initialize settings and finally call {@link Settings#start()}, e.g:
     * <pre>
     *     TinyLoki.withUrl(url)
     *         .withBasicAuth(user, pass)
     *         .withLogCollector(logCollector)
     *         .withLogMonitor(logMonitor)
     *         .start();
     * </pre>
     */
    public static LogController createAndStart(final String url, final String user, final String pass, final ILogCollector logCollector, ILogMonitor logMonitor) {
        return TinyLoki.withUrl(url).withBasicAuth(user, pass).withLogCollector(logCollector).withLogMonitor(logMonitor).start();
    }

    /**
     * Creates a configuration of LogController.
     *
     * @param url            URL to Loki HTTP API endpoint, usually ending with <code>/loki/api/v1/push</code>.
     * @param user           Basic authentication user. If null, BA header will not be sent.
     * @param pass           Basic authentication password. If null, BA header will not be sent.
     * @param connectTimeout HTTP log server connection timeout in milliseconds.
     * @param logCollector   {@link ILogCollector} instance.
     * @param logMonitor     {@link ILogMonitor } instance.
     * @return New {@link pl.mjaron.tinyloki.LogController LogController} object.
     * @since 0.2.1
     * @deprecated Use {@link TinyLoki#withUrl(String)} to initialize settings and finally call {@link Settings#start()}, e.g:
     * <pre>
     *     TinyLoki.withUrl(url)
     *         .withBasicAuth(user, pass)
     *         .withConnectTimeout(connectTimeout)
     *         .withLogCollector(logCollector)
     *         .withLogMonitor(logMonitor)
     *         .start();
     * </pre>
     */
    public static LogController createAndStart(final String url, final String user, final String pass, final int connectTimeout, final ILogCollector logCollector, ILogMonitor logMonitor) {
        return TinyLoki.withUrl(url).withBasicAuth(user, pass).withConnectTimeout(connectTimeout).withLogCollector(logCollector).withLogMonitor(logMonitor).start();
    }

    /**
     * Initialize a {@link pl.mjaron.tinyloki.Labels Labels} with predefined first label name-value. Use Labels.l() to
     * append next values.
     *
     * @param labelName  Label name.
     * @param labelValue Label value.
     * @return New Labels instance initialized with single label.
     * @since 0.2.2
     */
    public static Labels l(final String labelName, final String labelValue) {
        return new Labels().l(labelName, labelValue);
    }

    /**
     * Create labels from mapping.
     *
     * @param map Map containing label names and label values.
     * @return New Labels object initialized with labels stored in map.
     * @since 0.2.2
     */
    public static Labels l(final Map<String, String> map) {
        return new Labels().l(map);
    }

    /**
     * Create a deep copy of given labels.
     *
     * @param other Other labels instance.
     * @return Deep copy of labels.
     * @since 0.2.2
     */
    public static Labels l(final Labels other) {
        return new Labels(other);
    }

    /**
     * Creates new {@link Labels} with {@link Labels#LEVEL label level} set to {@link Labels#FATAL}.
     * <p>
     * The same as {@link #fatal()}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see Labels#LEVEL
     * @see Labels#FATAL
     * @since 0.2.1
     */
    public static Labels critical() {
        return new Labels().critical();
    }

    /**
     * Creates new {@link Labels} with {@link Labels#LEVEL label level} set to {@link Labels#FATAL}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see Labels#LEVEL
     * @see Labels#FATAL
     * @since 0.2.1
     */
    public static Labels fatal() {
        return new Labels().fatal();
    }

    /**
     * Creates new {@link Labels} with {@link Labels#LEVEL label level} set to {@link Labels#WARN}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see Labels#LEVEL
     * @see Labels#WARN
     * @since 0.2.1
     */
    public static Labels warning() {
        return new Labels().warning();
    }

    /**
     * Creates new {@link Labels} with {@link Labels#LEVEL label level} set to {@link Labels#INFO}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see Labels#LEVEL
     * @see Labels#INFO
     * @since 0.2.1
     */
    public static Labels info() {
        return new Labels().info();
    }

    /**
     * Creates new {@link Labels} with {@link Labels#LEVEL label level} set to {@link Labels#DEBUG}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see Labels#LEVEL
     * @see Labels#DEBUG
     * @since 0.2.1
     */
    public static Labels debug() {
        return new Labels().debug();
    }

    /**
     * Creates new {@link Labels} with {@link Labels#LEVEL label level} set to {@link Labels#VERBOSE}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see Labels#LEVEL
     * @see Labels#VERBOSE
     * @since 0.2.1
     */
    public static Labels verbose() {
        return new Labels().verbose();
    }

    /**
     * Creates new {@link Labels} with {@link Labels#LEVEL label level} set to {@link Labels#TRACE}.
     * <p>
     * The same as {@link #verbose()}
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see Labels#LEVEL
     * @see Labels#TRACE
     * @see Labels#VERBOSE
     * @since 0.2.1
     */
    public static Labels trace() {
        return new Labels().trace();
    }

    /**
     * Creates new {@link Labels} with {@link Labels#LEVEL label level} set to {@link Labels#UNKNOWN}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see Labels#LEVEL
     * @see Labels#UNKNOWN
     * @since 0.2.1
     */
    public static Labels unknown() {
        return new Labels().unknown();
    }
}