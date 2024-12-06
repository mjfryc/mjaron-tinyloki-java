package pl.mjaron.tinyloki;

/**
 * Dummy implementation of {@link ILogSender} for testing purposes.
 */
public class DummyLogSender implements ILogSender {

    private ILogMonitor logMonitor;
    private int dummySendBlockTime = 0;

    public DummyLogSender() {
    }

    public DummyLogSender(final int dummySendBlockTime) {
        if (dummySendBlockTime < 0) {
            throw new IllegalArgumentException("The block time must be 0 or positive value.");
        }
        this.dummySendBlockTime = dummySendBlockTime;
    }

    @Override
    public void configure(LogSenderSettings logSenderSettings, ILogMonitor logMonitor) {
        this.logMonitor = logMonitor;
    }

    @Override
    public void send(byte[] message) throws InterruptedException {
        logMonitor.send(message);
        if (dummySendBlockTime > 0) {
            Thread.sleep(dummySendBlockTime);
        }
        logMonitor.sendOk(200);
    }
}
