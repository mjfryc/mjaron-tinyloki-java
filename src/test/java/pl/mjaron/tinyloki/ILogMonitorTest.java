package pl.mjaron.tinyloki;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ILogMonitorTest {

    static Stream<ILogMonitor> sources() {
        return Stream.of(new VerboseLogMonitor(), new ErrorLogMonitor(), new SilentLogMonitor());
    }

    private static final byte[] encodedIn = new byte[]{0x00, 0x01, 0x02};
    private static final byte[] encodedOut = new byte[]{0x30, 0x31};

    @ParameterizedTest
    @MethodSource("sources")
    void basic(final ILogMonitor logMonitor) {
        logMonitor.onConfigured("CONTENT_TYPE", "CONTENT_ENCODING");
        logMonitor.onStart();
        logMonitor.onEncoded(encodedIn, encodedOut);
        logMonitor.send(encodedOut);
        logMonitor.sendOk(200);
        logMonitor.sendErr(404, "Bad");
        logMonitor.onException(new Exception("Sample exception"));
        logMonitor.logInfo("This is an info log.");
        logMonitor.logError("This is an error log.");
        logMonitor.onSync(true);
        logMonitor.onSync(false);
        logMonitor.onStop(true);
        logMonitor.onStop(false);
        assertTrue(true);
    }
}
