package pl.mjaron.tinyloki;

import java.nio.charset.StandardCharsets;

/**
 * This implementation is logging all communication between library and HTTP Loki server.
 */
public class VerboseLogMonitor extends ErrorLogMonitor {
    @Override
    public void send(final byte[] message) {
        System.out.println("<<< " + new String(message, StandardCharsets.UTF_8));
    }

    @Override
    public void sendOk(final int status) {
        System.out.println(">>> " + status);
    }
}
