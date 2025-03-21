package pl.mjaron.tinyloki;

/**
 * Provides the log occurrence timestamp in <b>nanoseconds</b>.
 * <p>
 * <b>Motivation</b><br>
 * Grafana Loki treats two identical log messages with the same timestamp as a single log.
 * The second log is ignored, even if the structured metadata is different.
 * <p>
 * User may want to differentiate the timestamps.
 * This is the reason why the creation of timestamp values is configurable.
 *
 * @see ITimestampProviderFactory
 * @see CurrentTimestampProvider
 * @see IncrementingTimestampProvider
 * @since 1.1.3
 */
public interface ITimestampProvider {

    /**
     * Provides log timestamp value in nanoseconds.
     * The point is to optionally avoid two identical log messages
     * having the same timestamp.
     * <p>
     * <b>Thread safety</b><br>
     * This method may not be thread safe and should be synchronized internally
     * by {@link ILogStream} or {@link ILogCollector} implementation.
     *
     * @param message Original log message. It may be analyzed by any {@link ITimestampProvider} implementation.
     * @return The calculated timestamp.
     * @since 1.1.3
     */
    long next(String message);
}
