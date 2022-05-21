package pl.mjaron.tinyloki;

import java.nio.charset.StandardCharsets;

/**
 * This implementation prints all communication between this library and HTTP Loki server.
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

    @Override
    public void onWorkerThreadExit(final boolean isSoft) {
        if (isSoft) {
            System.out.println("Worker thread exited correctly.");
        } else {
            System.err.println("Worker thread exited by interrupting.");
        }
    }
}
