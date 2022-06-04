package pl.mjaron.tinyloki;

/**
 * Deprecated class, kept for backward compatibility.
 *
 * @deprecated Use {@link HttpLogSender} instead.
 */
@Deprecated
public class LogSender extends HttpLogSender {

    /**
     * @deprecated Use {@link HttpLogSender} instead.
     */
    @Deprecated
    public LogSender() {
    }

    /**
     * Deprecated constructor, kept for backward compatibility.
     *
     * @param logSenderSettings Old way to pass {@link LogSenderSettings}.
     * @deprecated Use {@link HttpLogSender} instead.
     */
    @Deprecated
    public LogSender(@SuppressWarnings("unused") final LogSenderSettings logSenderSettings) {
    }
}
