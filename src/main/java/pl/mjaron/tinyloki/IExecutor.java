package pl.mjaron.tinyloki;

/**
 * Responsible for asynchronous flow of the collecting, encoding and sending the logs.
 * Initializes the asynchronous tread of other execution environment.
 *
 * @since 1.0.0
 */
public interface IExecutor {

    /**
     * Performs the configuration before using the object. Called internally by the {@link TinyLoki} constructor.
     *
     * @param logCollector Used to observe when new logs occur.
     * @param logProcessor Used to periodically perform the log processing.
     * @param logMonitor   Used to dump diagnostic logger information.
     * @since 1.0.0
     */
    void configure(ILogCollector logCollector, ILogProcessor logProcessor, ILogMonitor logMonitor);

    /**
     * Starts the execution loop.
     *
     * @since 1.0.0
     */
    void start();

    /**
     * Blocking function. Blocks the calling threat up to @param timeout milliseconds to send all collected logs to the server.
     *
     * @param timeout Maximum time to block the calling thread. After that time the function returns even the logs are not sent.
     * @return <code>true</code> If all logs has been processed by execution thread. Sending success / failure doesn't matter.
     * <p>
     * <code>false</code> If timeout has occurred.
     * @throws InterruptedException     When calling thread is interrupted.
     * @throws IllegalArgumentException If the value of <code>timeout</code> is negative.
     * @since 1.0.0
     */
    boolean sync(int timeout) throws InterruptedException;

    /**
     * Try to synchronize logs asynchronously.
     *
     * @since 1.0.0
     */
    void flush();

    /**
     * Blocking function. Stops the asynchronous service (thread) which sends the logs.
     * <p>
     * The thread is stopped asap and some logs may not be sent.
     *
     * @param timeout Maximum time to block the calling thread.
     *                <p>The <code>0</code> means wait forever until the worker thread is stopped.
     *                After that time the function returns even the thread is not stopped.
     * @return <code>true</code> If stopped with success.
     * <p>
     * <code>false</code> If failed to stop due to timeout.
     * @throws InterruptedException     When calling thread is interrupted.
     * @throws IllegalArgumentException If the value of <code>timeout</code> is negative.
     * @since 1.0.0
     */
    boolean stop(int timeout) throws InterruptedException;

    /**
     * Request to stopAnd without blocking the calling thread.
     * <p>
     * The {@link #stop(int)} still should be finally called.
     *
     * @since 1.0.0
     */
    void stopAsync();
}
