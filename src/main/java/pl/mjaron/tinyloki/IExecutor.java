package pl.mjaron.tinyloki;

/**
 * Responsible for asynchronous flow of the collecting, encoding and sending the logs.
 * Initializes the asynchronous tread of other execution environment.
 *
 * @since 0.4.0
 */
public interface IExecutor {

    /**
     * Performs the configuration before using the object. Called internally by the {@link LogController} constructor.
     *
     * @param logController The {@link LogController} object.
     * @since 0.4.0
     */
    void configure(LogController logController);

    /**
     * Starts the execution loop.
     *
     * @since 0.4.0
     */
    void start();

    /**
     * Blocking function. Blocks the calling threat up to @param timeout milliseconds to send all collected logs to the server.
     *
     * @param timeout Maximum time to block the calling thread. After that time the function returns even the logs are not sent.
     * @since 0.4.0
     */
    void sync(int timeout) throws InterruptedException;

    /**
     * Blocking function. Stops the asynchronous service (thread) which sends the logs.
     *
     * @param timeout Maximum time to block the calling thread. After that time the function returns event the thread is not stopped.
     * @since 0.4.0
     */
    void stop(int timeout) throws InterruptedException;
}
