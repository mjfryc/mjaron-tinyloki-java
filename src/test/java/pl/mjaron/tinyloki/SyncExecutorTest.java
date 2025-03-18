package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SyncExecutorTest {

    @Test
    void basic() throws InterruptedException {

        TinyLoki controller = TinyLoki.withUrl("http://example.com").withExecutor(new SyncExecutor()).withLogSender(new DummyLogSender(100)).withVerboseLogMonitor().start();
        ILogStream stream = controller.stream().l("color", "white").build();
        stream.log("My first log.");
        assertTrue(controller.closeSync());
    }

    @Test
    void onLog() {
        SyncExecutor executor = new SyncExecutor();
        executor.configure(new JsonLogCollector(), () -> {
            throw new InterruptedException();
        }, new VerboseLogMonitor());
        assertThrows(RuntimeException.class, () -> executor.onLog(15));
    }

    @Test
    void stopAsync() {
        SyncExecutor executor = new SyncExecutor();
        executor.stopAsync();
    }
}
