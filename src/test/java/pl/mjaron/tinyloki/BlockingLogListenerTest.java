package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class BlockingLogListenerTest {

    private static void trySleep(final int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void basicTest() throws InterruptedException {
        BlockingLogListener logListener = new BlockingLogListener();

        assertTimeout(Duration.ofMillis(100), () -> assertEquals(0, logListener.waitForLogs(10)));
    }


    @Test
    void syncFalse() throws InterruptedException {
        BlockingLogListener logListener = new BlockingLogListener();

        final Thread logWaitingLoop = new Thread(() -> {
            System.out.println("logWaitingLoop started!");
            trySleep(500);
            assertTimeout(Duration.ofMillis(100), () -> logListener.waitForLogs(10_000));
            System.out.println("logWaitingLoop finished!");
        });
        logWaitingLoop.start();

        assertTimeout(Duration.ofMillis(2_000), () -> assertFalse(logListener.sync(1_000)));
        logWaitingLoop.join(2_000);
    }

    @Test
    void syncTrue() throws InterruptedException {
        BlockingLogListener logListener = new BlockingLogListener();

        final Thread logWaitingLoop = new Thread(() -> {
            System.out.println("logWaitingLoop started!");
            trySleep(500);

            // The first occurrence will finish asap because the sync operation is in progress.
            assertTimeout(Duration.ofMillis(100), () -> logListener.waitForLogs(10_000));

            // The second occurrence will finish the sync operation but will wait all the time.
            assertTimeout(Duration.ofMillis(2_000), () -> logListener.waitForLogs(1_000));
            System.out.println("logWaitingLoop finished!");
        });
        logWaitingLoop.start();

        assertTimeout(Duration.ofMillis(700), () -> assertTrue(logListener.sync(700)));
        logWaitingLoop.join(0);
    }

    @Test
    void waitForLogs() {
        BlockingLogListener logListener = new BlockingLogListener();
        assertTimeout(Duration.ofMillis(1_000), () -> assertEquals(0, logListener.waitForLogs(500)));
    }

    @Test
    void flush() throws InterruptedException {
        BlockingLogListener logListener = new BlockingLogListener();
        logListener.onLog(100);
        Thread flushingThread = new Thread(() -> {
            try {
                Thread.sleep(100);
                logListener.flush();
            } catch (final InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        flushingThread.start();

        assertTimeout(Duration.ofMillis(500), () -> logListener.waitForLogs(100 * 1000));
        flushingThread.interrupt();
        flushingThread.join();
    }
}
