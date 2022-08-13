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
        System.out.println("LogController configured.");
        this.contentType = contentType;
        this.contentEncoding = contentEncoding;
    }

    @Override
    public void onEncoded(final byte[] in, final byte[] out) {
        System.out.println("<|> " + in.length + " bytes encoded to " + out.length + " bytes");
    }

    @Override
    public void send(final byte[] message) {
        if (contentEncoding == null && contentType.equals(JsonLogCollector.CONTENT_TYPE)) {
            System.out.println("<<< " + new String(message, StandardCharsets.UTF_8));
        }
        else {
            System.out.println("<<< " + message.length + " bytes sent");
        }
    }

    @Override
    public void sendOk(final int status) {
        System.out.println(">>> " + status);
    }

    @Override
    public void onWorkerThreadExit(final boolean isSoft) {
        if (isSoft) {
            System.out.println("Worker thread exited correctly.");
        } else {
            System.err.println("Worker thread exited by interrupting.");
        }
    }
}
