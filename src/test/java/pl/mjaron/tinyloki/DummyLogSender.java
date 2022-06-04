package pl.mjaron.tinyloki;

/**
 * Dummy implementation of {@link ILogSender} for testing purposes.
 */
public class DummyLogSender implements ILogSender {

    private ILogMonitor logMonitor;

    @Override
    public void configure(LogSenderSettings logSenderSettings, ILogMonitor logMonitor) {
        this.logMonitor = logMonitor;
    }

    @Override
    public void send(byte[] message) {
        logMonitor.send(message);
        logMonitor.sendOk(200);
    }
}
