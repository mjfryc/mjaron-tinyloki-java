package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ThreadExecutorTest {

    @Test
    void basic() throws InterruptedException {
        ThreadExecutor executor = new ThreadExecutor(1000);
        assertEquals(1000, executor.getLogCollectionPeriod());
        assertThrows(RuntimeException.class, () -> {
            executor.configure(null);
        });
        assertTimeout(Duration.ofMillis(100), () -> assertThrows(RuntimeException.class, executor::start));

        LogController controller = TinyLoki.withUrl("dummy").withExecutor(executor).withLogSender(new DummyLogSender()).withoutLogEncoder().start();
        assertSame(executor, controller.getExecutor());
        assertThrows(RuntimeException.class, executor::start);
        assertTimeout(Duration.ofMillis(200), () -> assertTrue(controller.stop(100)));
        assertTimeout(Duration.ofMillis(10), () -> assertTrue(controller.stop(1000)));
    }
}
