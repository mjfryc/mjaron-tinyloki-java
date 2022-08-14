package pl.mjaron.tinyloki;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * This implementation prints all communication between this library and HTTP Loki server.
 */
public class VerboseLogMonitor extends ErrorLogMonitor {

    private String contentType = null;
    private String contentEncoding = null;

    @Override
    public void onConfigured(final String contentType, final String contentEncoding) {
        ILogMonitor.systemLog("LogController configured.");
        this.contentType = contentType;
        this.contentEncoding = contentEncoding;
    }

    @Override
    public void onEncoded(final byte[] in, final byte[] out) {
        ILogMonitor.systemLog("<|> " + in.length + " bytes encoded to " + out.length + " bytes");
    }

    @Override
    public void send(final byte[] message) {
        if (contentEncoding == null && contentType.equals(JsonLogCollector.CONTENT_TYPE)) {
            ILogMonitor.systemLog("<<< " + new String(message, StandardCharsets.UTF_8));
        }
        else {
            ILogMonitor.systemLog("<<< " + message.length + " bytes sent");
        }
    }

    @Override
    public void sendOk(final int status) {
        ILogMonitor.systemLog(">>> " + status);
    }

    @Override
    public void onWorkerThreadExit(final boolean isSoft) {
        if (isSoft) {
            ILogMonitor.systemLog("Worker thread exited correctly.");
        } else {
            ILogMonitor.systemLog("Worker thread exited by interrupting.");
        }
    }
}
