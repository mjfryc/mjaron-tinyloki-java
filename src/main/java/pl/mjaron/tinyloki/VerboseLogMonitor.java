package pl.mjaron.tinyloki;

import java.nio.charset.StandardCharsets;

/**
 * This implementation prints all communication between this library and HTTP Loki server.
 */
public class VerboseLogMonitor implements ILogMonitor {

    private final boolean printMessages;
    private String contentType = null;
    private String contentEncoding = null;

    public VerboseLogMonitor() {
        this.printMessages = true;
    }

    public VerboseLogMonitor(final boolean printMessages) {
        this.printMessages = printMessages;
    }

    @Override
    public boolean isVerbose() {
        return true;
    }

    @Override
    public void logVerbose(String what) {
        ILogMonitor.printVerbose(what);
    }

    @Override
    public void logInfo(String what) {
        ILogMonitor.printInfo(what);
    }

    @Override
    public void logError(String what) {
        ILogMonitor.printError(what);
    }

    @Override
    public void onConfigured(final String contentType, final String contentEncoding) {
        ILogMonitor.printInfo("TinyLoki configured.");
        this.contentType = contentType;
        this.contentEncoding = contentEncoding;
    }

    @Override
    public void onEncoded(final byte[] in, final byte[] out) {
        ILogMonitor.printInfo("<|> " + in.length + " bytes encoded to " + out.length + " bytes");
    }

    @Override
    public void send(final byte[] message) {
        if (contentEncoding == null && contentType != null && contentType.equals(JsonLogCollector.CONTENT_TYPE)) {
            if (printMessages) {
                ILogMonitor.printInfo("<<< " + new String(message, StandardCharsets.UTF_8));
            } else {
                ILogMonitor.printInfo("<<< " + message.length + " bytes sent.");
            }
        } else {
            ILogMonitor.printInfo("<<< " + message.length + " bytes sent (encoding undefined).");
        }
    }

    @Override
    public void sendOk(final int status) {
        ILogMonitor.printInfo(">>> " + status);
    }

    @Override
    public void sendErr(int status, String message) {
        ILogMonitor.printError("Unexpected server response status: " + status + ": " + message);
    }

    @Override
    public void onException(Exception exception) {
        ILogMonitor.printError("Exception occurred: " + exception.toString() + "\n" + Utils.stackTraceString(exception));
    }

    @Override
    public void onSync(final boolean isSuccess) {
        if (isSuccess) {
            ILogMonitor.printInfo("Sync operation success.");
        } else {
            ILogMonitor.printError("Sync operation failed.");
        }
    }

    @Override
    public void onStart() {
        ILogMonitor.printInfo("Started.");
    }

    @Override
    public void onStop(final boolean isSuccess) {
        if (isSuccess) {
            ILogMonitor.printInfo("Stop operation success.");
        } else {
            ILogMonitor.printError("Stop operation failed.");
        }
    }
}
