package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ThreadExecutorTest {

    @Test
    void basic() throws InterruptedException {
        ThreadExecutor executor = new ThreadExecutor(1000);
        assertEquals(1000, executor.getProcessingIntervalTime());
        assertThrows(RuntimeException.class, () -> {
            executor.configure(null, new DummyLogProcessor(), new VerboseLogMonitor());
        });
        assertThrows(RuntimeException.class, () -> {
            executor.configure(new JsonLogCollector(), null, new VerboseLogMonitor());
        });
        assertThrows(RuntimeException.class, () -> executor.configure(new JsonLogCollector(), new DummyLogProcessor(), null));
        assertTimeout(Duration.ofMillis(100), () -> assertThrows(RuntimeException.class, executor::start));

        TinyLoki controller = TinyLoki.withUrl("dummy").withExecutor(executor).withLogSender(new DummyLogSender()).withoutLogEncoder().open();
        assertSame(executor, controller.getExecutor());
        assertThrows(RuntimeException.class, executor::start);
        assertTimeout(Duration.ofMillis(200), () -> assertTrue(controller.stop(100)));
        assertTimeout(Duration.ofMillis(10), () -> assertTrue(controller.stop(1000)));
    }

    @Test
    void logProcessorException() throws InterruptedException {
        final ILogProcessor throwingProcessor = new ILogProcessor() {
            private int counter = -1;
            private final int goodThreshold = 1;

            @Override
            public void processLogs() throws InterruptedException {
                ++counter;
                ILogMonitor.printInfo("[DummyLogsProcessor] Process logs loop: [" + counter + "].");
                if (counter < goodThreshold) {
                    throw new RuntimeException("[DummyLogsProcessor] Process logs exception (" + counter + "/" + goodThreshold + ")");
                }
            }
        };
        final ThreadExecutor executor = new ThreadExecutor(1000);
        final ILogCollector logCollector = new JsonLogCollector();
        logCollector.configureBufferingManager(new BasicBuffering());
        executor.configure(logCollector, throwingProcessor, new VerboseLogMonitor());
        executor.start();
        final ILogStream stream = logCollector.createStream(new Labels().l("sample", "label"));
        stream.log("Fish is changing everything (0)");
        Thread.sleep(2000);
        stream.log("Fish is changing everything (1)");
        assertTrue(executor.sync(100));
        assertTrue(executor.stop(100));
    }

    @Test
    void stopAsync() throws InterruptedException {
        ThreadExecutor executor = new ThreadExecutor(1000);
        executor.configure(new JsonLogCollector(), new DummyLogProcessor(), new SilentLogMonitor());
        executor.start();
        Thread.sleep(100);
        executor.stopAsync();
        Thread.sleep(100);

        //Thread should be already joined, so the short timeout should be enough.
        assertTrue(executor.stop(1));
        assertDoesNotThrow(executor::stopAsync);
    }

    static class TestLogCollector implements ILogCollector {

        private ILogListener logListener = null;

        public void callOnLog(final int cachedLogsCount) {
            logListener.onLog(cachedLogsCount);
        }

        @Override
        public void configureLogListener(ILogListener logListener) {
            this.logListener = logListener;
        }

        @Override
        public void configureBufferingManager(IBuffering bufferingManager) {
        }

        @Override
        public void configureStructuredMetadata(LabelSettings structuredMetadataLabelSettings) {
        }

        @Override
        public ILogStream createStream(Labels labels) {
            return null;
        }

        @Override
        public byte[] collect() {
            return new byte[0];
        }

        @Override
        public byte[][] collectAll() {
            return new byte[0][];
        }

        @Override
        public String contentType() {
            return "";
        }
    }

    static class BlockingLogProcessor implements ILogProcessor {

        private final int blockingTime;
        private final AtomicBoolean exit = new AtomicBoolean(false);

        public BlockingLogProcessor(final int blockingTime) {
            this.blockingTime = blockingTime;
        }

        public void stop() {
            exit.set(true);
            //System.out.println("[BlockingLogProcessor] Stop requested...");
        }

        @Override
        public void processLogs() throws InterruptedException {
            final long deadline = Utils.MonotonicClock.timePoint(blockingTime);

            while (!exit.get() && Utils.MonotonicClock.timePoint() < deadline) {
                try {
                    Thread.sleep(1);
                } catch (final InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("[BlockingLogProcessor] Exiting. Exit: " + exit.get());
        }
    }

    @Test
    void stopTimeout() throws InterruptedException {
        final TestLogCollector logCollector = new TestLogCollector();
        final BlockingLogProcessor blockingLogProcessor = new BlockingLogProcessor(10 * 1000);

        // 1st second: Thread executor sleeps 1 second waiting for logs...
        ThreadExecutor executor = new ThreadExecutor(1000);
        executor.configure(logCollector, blockingLogProcessor, new SilentLogMonitor());
        executor.start();

        // 1st second: The log occurs and the blockingLogProcessor hangs the application for 10 seconds.
        logCollector.callOnLog(13);
        Thread.sleep(2000);

        // ... The blockingLogProcessor still hands the application here...
        // Trying to stop the executor but the BlockingLogProcessor is stupid and ignores thread interruption exceptions...
        // Expecting that stopping will fail and executor will keep this bad thread set, because it is still not released.
        final boolean stopResult = executor.stop(1);
        assertFalse(stopResult);

        // Requesting this stupid log processor to exit,
        blockingLogProcessor.stop();
        //System.out.println("Stop(2)...");

        // Now the executor should finish the thread correctly and return true (success).
        final boolean stopResult2 = executor.stop(10000);
        //System.out.println("Stop(2)...done.");
        assertTrue(stopResult2);
    }

    @Test
    void stopRace() throws InterruptedException {
        ThreadExecutor executor = new ThreadExecutor(1000);
        executor.configure(new JsonLogCollector(), new DummyLogProcessor(), new SilentLogMonitor());
        executor.start();
        Thread.sleep(100);

        AtomicInteger successStopCount = new AtomicInteger(0);

        final int threadsCount = 20;
        ExecutorService executors = Executors.newFixedThreadPool(threadsCount);
        final long startTimePoint = Utils.MonotonicClock.timePoint(200);
        for (int i = 0; i < threadsCount; ++i) {
            executors.submit(() -> {
                while (Utils.MonotonicClock.timePoint() < startTimePoint) {
                    int sth = 2;
                    sth -= 6;
                    //noinspection ConstantValue
                    if (sth > 5) {
                        return;
                    }
                }

                try {
                    if (executor.stop(100)) {
                        successStopCount.incrementAndGet();
                    }
                } catch (final InterruptedException ignored) {
                }
            });
        }

        Thread.sleep(1000);
        executors.shutdown();
        assertTrue(executors.awaitTermination(1000, TimeUnit.MILLISECONDS));
        assertEquals(20, successStopCount.get());

    }

    @Test
    void asyncStartStop() throws InterruptedException {
        ThreadExecutor executor = new ThreadExecutor(1000);
        executor.configure(new JsonLogCollector(), new DummyLogProcessor(), new SilentLogMonitor());
        Thread t0 = new Thread(() -> {
            Random random = new Random();
            int good = 0;
            int fail = 0;
            int exceptionLimit = 1;
            while (true) {
                try {
                    try {
                        executor.start();
                        ++good;
                    } catch (final RuntimeException e) {
                        ++fail;
                        if (exceptionLimit > 0) {
                            --exceptionLimit;
                            System.out.println("Start exception:\n" + e.toString());
                        }
                    }

                    Thread.sleep(random.nextInt(100));
                } catch (final InterruptedException e) {
                    System.out.println("Start operations: Good: " + good + "/" + (good + fail) + ".");
                    return;
                }
            }
        });

        Thread t1 = new Thread(() -> {
            Random random = new Random();
            int good = 0;
            int fail = 0;
            int exceptionLimit = 1;
            while (true) {
                try {
                    try {
                        executor.stop(random.nextInt(100));
                        ++good;
                    } catch (final RuntimeException e) {
                        ++fail;
                        if (exceptionLimit > 0) {
                            --exceptionLimit;
                            System.out.println("Stop exception:\n" + e.toString());
                        }
                    }

                    Thread.sleep(random.nextInt(100));
                } catch (final InterruptedException e) {
                    System.out.println("Stop operations: Good: " + good + "/" + (good + fail) + ".");
                    return;
                }
            }
        });

        t0.start();
        t1.start();
        final int secondsCount = 10;
        for (int i = 0; i < secondsCount; ++i) {
            System.out.println("Waiting " + i + "/" + secondsCount + "...");
            Thread.sleep(1000);
        }
        System.out.println("Interrupting threads...");

        t0.interrupt();
        t1.interrupt();
        t0.join();
        t1.join();
        final boolean lastStop = executor.stop(100);
        System.out.println("Last executor sopped with success: " + lastStop);
        assertTrue(lastStop);
        System.out.println("Done.");
    }
}
