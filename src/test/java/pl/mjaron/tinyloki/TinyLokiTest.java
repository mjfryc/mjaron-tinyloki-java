package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class TinyLokiTest {

    @Test
    void dummySendLegacyTest() {
        LogController logController = TinyLoki.withUrl("http://localhost/loki/api/v1/push").withLogSender(new DummyLogSender(1000)).withLogMonitor(new VerboseLogMonitor()).withLabelLength(1, 1).start();
        ILogStream abcStream = logController.createStream(TinyLoki.info().l("abc", "bcd"));
        abcStream.log(1, "Hello world.");
        logController.softStop().hardStop();
    }

    @Test
    void dummySendTest() throws InterruptedException {
        LogController logController = TinyLoki.withUrl("http://localhost/loki/api/v1/push").withLogSender(new DummyLogSender(LogController.DEFAULT_SYNC_TIMEOUT - 100)).withLogMonitor(new VerboseLogMonitor()).withLabelLength(1, 1).withExecutor(new ThreadExecutor(10 * 1000)).start();
        ILogStream abcStream = logController.stream().info().l("abc", "bcd").build();
        abcStream.log(1, "Hello world.");
        long t0 = System.currentTimeMillis();
        final boolean result = logController.closeSync(LogController.DEFAULT_SYNC_TIMEOUT, 3000);
        System.out.println("closeSync time: " + (System.currentTimeMillis() - t0));
        assertTrue(result);
        System.out.println("Done.");
    }

    @Test
    @Disabled
    void tinyLokiTest() throws InterruptedException {
        LogController logController = TinyLoki.withUrl("http://localhost:3100/loki/api/v1/push").withBasicAuth("user", "pass").withConnectTimeout(5000).start();
        ILogStream stream = logController.stream().info().l("host", "ZEUS").build();
        //Or: ILogStream stream = logController.createStream(TinyLoki.info().l("host", "ZEUS"));
        stream.log("Hello world.");
        // ... new streams and other logs here.
        boolean closedWithSuccess = logController.closeSync(1000);
        assertTrue(closedWithSuccess);
    }

    @Test
    void settingsTest() {
        ILogCollector collector = new JsonLogCollector();
        ILogEncoder logEncoder = new ILogEncoder() {
            @Override
            public String contentEncoding() {
                return "NO_ENCODING";
            }

            @Override
            public byte[] encode(byte[] what) throws IOException {
                return what;
            }
        };
        TinyLoki.Settings settings = TinyLoki.withUrl("http://example.com").withBasicAuth("aaa", "bbb").withConnectTimeout(123).withLogCollector(collector).withLogEncoder(logEncoder);
        assertEquals("aaa", settings.getLogSenderSettings().getUser());
        assertEquals("bbb", settings.getLogSenderSettings().getPassword());
        assertEquals(123, settings.getLogSenderSettings().getConnectTimeout());
        assertSame(collector, settings.getLogCollector());
        assertSame(logEncoder, settings.getLogEncoder());
        settings.withGzipLogEncoder();
        assertEquals(GzipLogEncoder.class, settings.getLogEncoder().getClass());

        ILogMonitor logMonitor = new SilentLogMonitor();
        settings.withLogMonitor(logMonitor);
        assertSame(logMonitor, settings.getLogMonitor());

        settings.withErrorLogMonitor();
        assertEquals(ErrorLogMonitor.class, settings.getLogMonitor().getClass());

        settings.withVerboseLogMonitor();
        assertEquals(VerboseLogMonitor.class, settings.getLogMonitor().getClass());

        ILogSender logSender = new MemoryLogSender();
        settings.withLogSender(logSender);
        assertSame(logSender, settings.getLogSender());
    }

    @Test
    @Disabled
    void sampleTest() throws InterruptedException {
        try (LogController loki = TinyLoki.withUrl("http://localhost:3100/loki/api/v1/push").withVerboseLogMonitor().start()) {
            ILogStream stream = loki.stream().l("color", "white").build();
            stream.log("Hello world.");
            loki.sync();
            loki.stop();
        }
    }

    static class MassiveThread extends Thread {
        int threadId;
        ArrayList<ILogStream> streams;
        int count = 0;

        public MassiveThread(int threadId, ArrayList<ILogStream> streams) {
            this.threadId = threadId;
            this.streams = streams;
        }

        @Override
        public void run() {
            System.out.println("Starting thread: " + threadId);
            final Random random = new Random();
            while (true) {
                final int streamIdx = random.nextInt(streams.size());
                streams.get(streamIdx).log("Hello entry: thread: [" + threadId + "], entry: [" + count + "].");
                ++count;
                try {
                    Thread.sleep(random.nextInt(10));
                } catch (InterruptedException ignored) {
                    return;
                }
            }
        }
    }

    @Test
    @Disabled
    void sampleMassiveTest() throws InterruptedException {
        List<MassiveThread> threads = new ArrayList<>();
        ArrayList<ILogStream> streams = new ArrayList<>();
        final Random random = new Random();
        final int testRunId = random.nextInt(1000);
        System.out.println("Running test: " + testRunId);

        try (LogController loki = TinyLoki.withUrl("http://localhost:3100/loki/api/v1/push").withVerboseLogMonitor(false).start()) {

            for (int i = 0; i < 100; ++i) {
                streams.add(loki.stream().l("test_name", "sampleMassiveTest").l("test_run_id", "test_" + testRunId).l("test_stream_index", "index_" + i).build());
            }

            for (int i = 0; i < 100; ++i) {
                MassiveThread thr = new MassiveThread(i, streams);
                thr.start();
                threads.add(thr);
            }

            Thread.sleep(6 * 1000);

            System.out.println("Interrupting threads...");
            for (Thread thread : threads) {
                thread.interrupt();
            }

            System.out.println("Joining threads...");
            for (Thread thread : threads) {
                thread.join(1000);
            }

            System.out.println("Syncing...");
            loki.sync(10 * 1000);
            loki.stop();

            System.out.println("Stopped.");

            int sum = 0;
            for (MassiveThread thr : threads) {
                sum += thr.count;
            }

            System.out.println("Done: " + testRunId + " + Total logs: " + sum);
        }
    }
}
