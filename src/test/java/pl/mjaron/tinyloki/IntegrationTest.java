package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Modify the run configuration by adding environment variable:
 * <pre>{@code
 * TINYLOKI_INTEGRATION=1
 * }</pre>
 */
@EnabledIfEnvironmentVariable(named = "TINYLOKI_INTEGRATION", matches = "1")
public class IntegrationTest {

    public static boolean isIntegrationEnabled() {
        return false;
    }

    @Test
    void shortExample() throws InterruptedException {
        TinyLoki loki = TinyLoki.withUrl("http://localhost:3100")
                .withBasicAuth("user", "pass")
                .start();

        ILogStream windowStream = loki.stream().info().l("device", "window").build();
        ILogStream doorStream = loki.stream().info().l("device", "door").build();

        windowStream.log("The window is open.");
        doorStream.log("The door is open.");

        // Time for syncing and closing the logs.
        boolean closedWithSuccess = loki.closeSync(1000);

        System.out.println("Closed with success: " + closedWithSuccess);

        assertTrue(closedWithSuccess);
    }

    @Test
    void sampleTest() throws InterruptedException {
        try (TinyLoki loki = TinyLoki.withUrl("http://localhost:3100/loki/api/v1/push").withVerboseLogMonitor().start()) {
            ILogStream stream = loki.stream().l("color", "white").build();
            stream.log("Hello world.");
            loki.sync();
            loki.stop();
        }
    }


    static class MassiveThread extends Thread {
        static final public String LABEL_STREAM_IDX = "test_stream_index";

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
                final ILogStream stream = streams.get(streamIdx);
                stream.log("Hello entry: thread: [" + threadId + "], entry: [" + count + "]. This is a sample log placeholder. This is a sample log placeholder. This is a sample log placeholder. This is a sample log placeholder.");
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
    void sampleMassiveTest() throws InterruptedException {
        List<MassiveThread> threads = new ArrayList<>();
        ArrayList<ILogStream> streams = new ArrayList<>();
        final Random random = new Random();
        final int testRunId = random.nextInt(1000);
        System.out.println("Running test: " + testRunId);

        try (TinyLoki loki = TinyLoki.withUrl("http://localhost:3100/loki/api/v1/push").withVerboseLogMonitor(false).withExecutor(new ThreadExecutor(100000)).start()) {

            for (int i = 0; i < 100; ++i) {
                streams.add(loki.stream().l("test_name", "sampleMassiveTest").l("test_run_id", "test_" + testRunId).l(MassiveThread.LABEL_STREAM_IDX, "index_" + i).build());
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

            System.out.println("Done: test_run_id: test_" + testRunId + ", Total logs: " + sum);
        }
    }
}
