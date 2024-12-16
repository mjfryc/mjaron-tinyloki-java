package pl.mjaron.tinyloki;

/**
 * Prints only error events.
 */
public class ErrorLogMonitor implements ILogMonitor {

    @Override
    public void logInfo(final String what) {
        ILogMonitor.printInfo(what);
    }

    @Override
    public void logError(final String what) {
        ILogMonitor.printError(what);
    }

    @Override
    public void onConfigured(final String contentType, final String contentEncoding) {
    }

    @Override
    public void onEncoded(final byte[] in, final byte[] out) {
    }

    @Override
    public void send(final byte[] message) {
    }

    @Override
    public void sendOk(final int status) {
    }

    @Override
    public void sendErr(final int status, final String message) {
        ILogMonitor.printError("Unexpected server response status: " + status + ": " + message);
    }

    @Override
    public void onException(final Exception exception) {
        ILogMonitor.printError("Exception occurred: " + exception.toString() + "\n" + Utils.stackTraceString(exception));
    }

    @Override
    public void onSync(final boolean isSuccess) {
        if (!isSuccess) {
            ILogMonitor.printError("Sync operation failed.");
        }
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onStop(final boolean isSuccess) {
        if (!isSuccess) {
            ILogMonitor.printError("Stop operation failed.");
        }
    }
}

