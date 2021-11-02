package pl.mjaron.tinyloki;

/**
 * Factory method for common objects.
 */
@SuppressWarnings("unused")
public class TinyLoki {

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
        LogSenderSettings logSenderSettings = LogSenderSettings.create().setUrl(url);
        logSenderSettings.setUser(user);
        logSenderSettings.setPassword(pass);

        return new LogController(
                logCollector,
                new LogSender(logSenderSettings),
                logMonitor).start();
    }

    /**
     * Creates a basic configuration of LogController.
     *
     * @param url  URL to Loki HTTP API endpoint, usually ending with `/loki/api/v1/push`.
     * @param user Basic authentication user. If null, BA header will not be sent.
     * @param pass Basic authentication password. If null, BA header will not be sent.
     * @return New {@link pl.mjaron.tinyloki.LogController LogController} object.
     */
    public static LogController createAndStart(final String url, final String user, final String pass) {
        return createAndStart(url, user, pass, new JsonLogCollector(), new ErrorLogMonitor());
    }

    /**
     * Initialize a {@link pl.mjaron.tinyloki.Labels Labels} with predefined first label name-value. Use Labels.l() to
     * append next values.
     *
     * @param labelName  Label name.
     * @param labelValue Label value.
     * @return New Labels instance.
     */
    public static Labels l(final String labelName, final String labelValue) {
        return new Labels().l(labelName, labelValue);
    }
}
