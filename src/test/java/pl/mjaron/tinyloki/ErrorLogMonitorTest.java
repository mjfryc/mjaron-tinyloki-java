package pl.mjaron.tinyloki;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ErrorLogMonitorTest {

    @Test
    void basic() {
        ErrorLogMonitor e = new ErrorLogMonitor();
        e.onConfigured("CONTENT_TYPE", "CONTENT_ENCODING");
        e.onEncoded(null, null);
        e.send(null);
        e.sendOk(200);
        e.sendErr(404, "Bad");
        e.onException(new Exception("Sample exception"));
        e.onWorkerThreadExit(false);
        e.onWorkerThreadExit(true);
        assertTrue(true);
    }
}