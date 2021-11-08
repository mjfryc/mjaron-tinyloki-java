package pl.mjaron.tinyloki;

import java.util.Map;

/**
 * Factory methods for common objects.
 */
@SuppressWarnings("unused")
public class TinyLoki {

    /**
     * Creates a basic configuration of LogController.
     *
     * @param url  URL to Loki HTTP API endpoint, usually ending with `/loki/api/v1/push`.
     * @param user Basic authentication user. If null, BA header will not be sent.
     * @param pass Basic authentication password. If null, BA header will not be sent.
     * @return New {@link pl.mjaron.tinyloki.LogController LogController} object.
     */
    public static LogController createAndStart(final String url, final String user, final String pass) {
        return createAndStart(url, user, pass, LogSenderSettings.DEFAULT_CONNECT_TIMEOUT);
    }

    /**
     * Creates a basic configuration of LogController.
     *
     * @param url            URL to Loki HTTP API endpoint, usually ending with `/loki/api/v1/push`.
     * @param user           Basic authentication user. If null, BA header will not be sent.
     * @param pass           Basic authentication password. If null, BA header will not be sent.
     * @param connectTimeout HTTP log server connection timeout in milliseconds.
     * @return New {@link pl.mjaron.tinyloki.LogController LogController} object.
     */
    public static LogController createAndStart(final String url, final String user, final String pass, final int connectTimeout) {
        return createAndStart(url, user, pass, connectTimeout, new JsonLogCollector(), new ErrorLogMonitor());
    }

    /**
     * Creates a configuration of LogController.
     *
     * @param url          URL to Loki HTTP API endpoint, usually ending with `/loki/api/v1/push`.
     * @param user         Basic authentication user. If null, BA header will not be sent.
     * @param pass         Basic authentication password. If null, BA header will not be sent.
     * @param logCollector {@link pl.mjaron.tinyloki.ILogCollector ILogCollector} instance.
     * @param logMonitor   {@link pl.mjaron.tinyloki.ILogMonitor ILogMonitor} instance.
     * @return New {@link pl.mjaron.tinyloki.LogController LogController} object.
     */
    public static LogController createAndStart(final String url, final String user, final String pass, final ILogCollector logCollector, ILogMonitor logMonitor) {
        return createAndStart(url, user, pass, LogSenderSettings.DEFAULT_CONNECT_TIMEOUT, logCollector, logMonitor);
    }

    /**
     * Creates a configuration of LogController.
     *
     * @param url            URL to Loki HTTP API endpoint, usually ending with `/loki/api/v1/push`.
     * @param user           Basic authentication user. If null, BA header will not be sent.
     * @param pass           Basic authentication password. If null, BA header will not be sent.
     * @param connectTimeout HTTP log server connection timeout in milliseconds.
     * @param logCollector   {@link ILogCollector} instance.
     * @param logMonitor     {@link ILogMonitor } instance.
     * @return New {@link pl.mjaron.tinyloki.LogController LogController} object.
     */
    public static LogController createAndStart(final String url, final String user, final String pass, final int connectTimeout, final ILogCollector logCollector, ILogMonitor logMonitor) {
        LogSenderSettings logSenderSettings = LogSenderSettings.create().setUrl(url);
        logSenderSettings.setUser(user);
        logSenderSettings.setPassword(pass);
        logSenderSettings.setConnectTimeout(connectTimeout);

        return new LogController(
                logCollector,
                new LogSender(logSenderSettings),
                logMonitor).start();
    }

    /**
     * Initialize a {@link pl.mjaron.tinyloki.Labels Labels} with predefined first label name-value. Use Labels.l() to
     * append next values.
     *
     * @param labelName  Label name.
     * @param labelValue Label value.
     * @return New Labels instance initialized with single label.
     */
    public static Labels l(final String labelName, final String labelValue) {
        return new Labels().l(labelName, labelValue);
    }

    /**
     * Create labels from mapping.
     *
     * @param map Map containing label names and label values.
     * @return New Labels object initialized with labels stored in map.
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