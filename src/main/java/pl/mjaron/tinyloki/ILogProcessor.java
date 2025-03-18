package pl.mjaron.tinyloki;

/**
 * Provides an implementation of collecting and sending the logs. Called periodically by {@link IExecutor}.
 *
 * @since 1.0.0
 */
public interface ILogProcessor {

    /**
     * Called periodically by {@link IExecutor}.
     * <p>
     * <b>Thread safety</b>
     * <p>
     * The method is not thread safe. Should be called sequentially.
     *
     * @throws InterruptedException When thread is interrupted.
     * @since 1.0.0
     */
    void processLogs() throws InterruptedException;
}
