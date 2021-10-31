package pl.mjaron.tinyloki;

public class LogSenderSettings {
    private String url = null;
    private String user = null;
    private String password = null;
    private String contentType = "application/json";

    public static LogSenderSettings create() {
        return new LogSenderSettings();
    }

    public String getUrl() {
        return url;
    }

    public LogSenderSettings setUrl(final String url) {
        this.url = url;
        return this;
    }

    public String getUser() {
        return user;
    }

    public LogSenderSettings setUser(final String user) {
        this.user = user;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public LogSenderSettings setPassword(final String password) {
        this.password = password;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public LogSenderSettings setContentType(final String contentType) {
        this.contentType = contentType;
        return this;
    }
}
