package pl.mjaron.tinyloki;

/**
 * Prints only error events.
 */
public class ErrorLogMonitor implements ILogMonitor {

    @Override
    public void onConfigured(final String contentType, final String contentEncoding) {
    }

    @Override
    public void onEncoded(byte[] in, byte[] out) {
    }

    @Override
    public void send(final byte[] message) {
    }

    @Override
    public void sendOk(final int status) {
    }

    @Override
    public void sendErr(final int status, final String message) {
        System.err.println("Unexpected server response status: " + status + ": " + message);
    }

    @Override
    public void onException(Exception exception) {
        exception.printStackTrace();
    }

    @Override
    public void onWorkerThreadExit(final boolean isSoft) {
        if (!isSoft) {
            System.err.println("Worker thread exited by interrupting.");
        }
    }
}

