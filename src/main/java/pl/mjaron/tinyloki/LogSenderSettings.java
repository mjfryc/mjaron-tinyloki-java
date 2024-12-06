package pl.mjaron.tinyloki;

/**
 * Used to configure {@link HttpLogSender}.
 */
@SuppressWarnings("UnusedReturnValue")
public class LogSenderSettings {

    /**
     * Default timeout for log server connecting in milliseconds.
     */
    public final static int DEFAULT_CONNECT_TIMEOUT = 5000;

    /**
     * HTTP connection URL.
     */
    private String url = null;

    /**
     * HTTP Basic Authentication user.
     */
    private String user = null;

    /**
     * HTTP Basic Authentication password.
     */
    private String password = null;

    /**
     * Complaint with HTTP Content-Type header.
     */
    private String contentType = "application/json";

    /**
     * Complaint with HTTP Content-Encoding header.
     * Used to send Json compressed with Gzip.
     *
     * @since 0.3.4
     */
    private String contentEncoding = null;

    /**
     * Default value of connecting timeout.
     * User can change connecting timeout by calling {@link #setConnectTimeout(int)}.
     */
    private int connectTimeout = DEFAULT_CONNECT_TIMEOUT;

    /**
     * Creates a new instance of {@link LogSenderSettings}.
     * It may be used instead of constructor.
     *
     * @return New instance of {@link LogSenderSettings}.
     */
    public static LogSenderSettings create() {
        return new LogSenderSettings();
    }

    /**
     * Getter of HTTP URL used to connect to Grafana Loki server.
     * This URL's path should end with `/loki/api/v1/push`.
     *
     * @return HTTP URL used to connect to Grafana Loki server.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Setter of HTTP URL used to connect to Grafana Loki server.
     * This URL's path should end with `/loki/api/v1/push`.
     *
     * @param url HTTP URL used to connect to Grafana Loki server.
     *            This URL's path should end with `/loki/api/v1/push`.
     * @return This reference.
     */
    public LogSenderSettings setUrl(final String url) {
        this.url = url;
        return this;
    }

    /**
     * Getter of HTTP Basic Authentication user.
     *
     * @return HTTP Basic Authentication user.
     */
    public String getUser() {
        return user;
    }

    /**
     * Setter of HTTP Basic Authentication user.
     *
     * @param user HTTP Basic Authentication user.
     * @return This reference.
     */
    public LogSenderSettings setUser(final String user) {
        this.user = user;
        return this;
    }

    /**
     * Getter of HTTP Basic Authentication password.
     *
     * @return HTTP Basic Authentication password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Setter of HTTP Basic Authentication password.
     *
     * @param password HTTP Basic Authentication password.
     * @return This reference.
     */
    public LogSenderSettings setPassword(final String password) {
        this.password = password;
        return this;
    }

    /**
     * Getter of HTTP Content-Type header value.
     *
     * @return HTTP Content-Type header value.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Setter of HTTP Content-Type header value.
     *
     * @param contentType HTTP Content-Type header value.
     * @return This reference.
     */
    public LogSenderSettings setContentType(final String contentType) {
        this.contentType = contentType;
        return this;
    }

    /**
     * Getter of HTTP Content-Encoding header value.
     * It may be null when not set.
     *
     * @return HTTP Content-Encoding header value.
     * @since 0.3.4
     */
    public String getContentEncoding() {
        return contentEncoding;
    }

    /**
     * Setter of HTTP Content-Encoding header value.
     *
     * @param contentEncoding HTTP Content-Encoding header value.
     * @return This reference.
     * @since 0.3.4
     */
    public LogSenderSettings setContentEncoding(final String contentEncoding) {
        this.contentEncoding = contentEncoding;
        return this;
    }

    /**
     * HTTP connecting timeout getter.
     *
     * @return HTTP connecting timeout in milliseconds.
     */
    public int getConnectTimeout() {
        return connectTimeout;
    }

    /**
     * Setter of timeout when connecting to the HTTP server.
     *
     * @param connectTimeout Time in milliseconds.
     * @return This reference.
     */
    @SuppressWarnings("unused")
    public LogSenderSettings setConnectTimeout(final int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }
}
