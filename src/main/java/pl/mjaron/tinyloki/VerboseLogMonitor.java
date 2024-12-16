package pl.mjaron.tinyloki;

import java.nio.charset.StandardCharsets;

/**
 * This implementation prints all communication between this library and HTTP Loki server.
 */
public class VerboseLogMonitor extends ErrorLogMonitor {

    private String contentType = null;
    private String contentEncoding = null;

    @Override
    public void onConfigured(final String contentType, final String contentEncoding) {
        ILogMonitor.printInfo("LogController configured.");
        this.contentType = contentType;
        this.contentEncoding = contentEncoding;
    }

    @Override
    public void onEncoded(final byte[] in, final byte[] out) {
        ILogMonitor.printInfo("<|> " + in.length + " bytes encoded to " + out.length + " bytes");
    }

    @Override
    public void send(final byte[] message) {
        if (contentEncoding == null && contentType.equals(JsonLogCollector.CONTENT_TYPE)) {
            ILogMonitor.printInfo("<<< " + new String(message, StandardCharsets.UTF_8));
        } else {
            ILogMonitor.printInfo("<<< " + message.length + " bytes sent");
        }
    }

    @Override
    public void sendOk(final int status) {
        ILogMonitor.printInfo(">>> " + status);
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
