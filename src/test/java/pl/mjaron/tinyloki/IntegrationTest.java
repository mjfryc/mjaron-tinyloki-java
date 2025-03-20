package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * To enable this test, modify the run configuration by adding environment variable:
 * <pre>{@code
 * TINYLOKI_INTEGRATION=1
 * }</pre>
 * <p>
 * Integration test server must be enabled to receive logs, see <code>integration-test-server</code> project directory.
 */
@EnabledIfEnvironmentVariable(named = "TINYLOKI_INTEGRATION", matches = "1")
public class IntegrationTest {

    public static boolean isIntegrationEnabled() {
        return false;
    }

    @Test
    void shortExample() throws InterruptedException {
        TinyLoki loki = TinyLoki.withUrl("http://localhost:3100").withBasicAuth("user", "pass").open();
        ILogStream logStream = loki.stream().info().l("topic", "shortExample").open();
        logStream.log("Hello world!");
        logStream.log("Hello world!", Labels.of("structured_metadata", "My log metadata value."));
        loki.closeSync();
    }

    @Test
    void verboseExample() throws InterruptedException {

        // Initialize the log controller instance with URL.
        // The endpoint loki/api/v1/push will be added by default if missing.
        // Usually creating more than one TinyLoki instance doesn't of sense.
        // TinyLoki (its default IExecutor implementation) owns separate thread which sends logs periodically.
        // It may be called inside try-with-resources block, but the default close() method doesn't synchronize the logs,
        // but just interrupts the background worker thread.
        try (TinyLoki loki = TinyLoki.withUrl("http://localhost:3100/loki/api/v1/push")

                // Print all diagnostic information coming from the TinyLoki library. For diagnostic purposes only.
                // The messages are printed only if there is no log encoder (comment out .withGzipLogEncoder())
                .withVerboseLogMonitor(true)

                // Set the custom log processing interval time.
                // So the executor will try to send the next logs 10 seconds after the previous logs sending operation.
                .withThreadExecutor(10 * 1000)

                // Set custom time of HTTP connection establishing timeout.
                .withConnectTimeout(10 * 1000)

                // Encode the logs to limit the size of data sent.
                // .withGzipLogEncoder()

                // The BasicBuffering is set by default, but here the (not encoded) message size limit may be customized.
                .withBasicBuffering(3 * 1024 * 1024, 10)

                // Initialize the library with above settings.
                // The ThreadExecutor will create a new thread and start waiting for the logs to be sent.
                .open()) {

            // Some logs here...

            // Let's define some labels common for few streams.
            Labels topic = Labels.of("topic", "verboseExample");

            ILogStream topicStream = loki.stream().l(topic).open();
            topicStream.log("Hello world.");

            ILogStream whiteStream = loki.stream().l(topic).l("topic", "verboseExample").l("color", "white").open();
            whiteStream.log("Hello white world.");

            // Blocking method, tries to send the logs ASAP and wait for sending completion.
            // This method returns false when timeout occurs, but true when sending has completed with success or failure.
            boolean allHttpSendingOperationsFinished = loki.sync();
            System.out.println("Are all logs processed: " + allHttpSendingOperationsFinished);

            ILogStream redStream = loki.stream().l(topic).l("color", "red").open();

            // Let's attach the Grafana Loki structured metadata.
            // In current implementation, the duplicated logs with same log line and timestamp (structured metadata doesn't matter) - is sent but may be dropped by Grafana Loki.
            redStream.log("Hello red world 0", Labels.of("structured_metadata_label", 0).l("other_structured_metadata_label", 'a'));
            redStream.log("Hello red world 1", Labels.of("structured_metadata_label", 9).l("other_structured_metadata_label", 'z'));

            // Blocking method, tries to synchronize the logs than interrupt and join the execution thread.
            // Set the custom timeout time for this operation.
            boolean closedWithSuccess = loki.closeSync(5 * 1000);

            System.out.println("Synced and closed with success: " + closedWithSuccess);
        }
    }

    @Test
    void structuredMetadataTest() throws InterruptedException {
        TinyLoki loki = TinyLoki.withUrl("http://localhost:3100").withVerboseLogMonitor(true).withBasicAuth("user", "pass").open();
        ILogStream helloStream = loki.stream().info().l("topic", "structuredMetadataTest").l("Number", 3).open();
        helloStream.log("Hello world!", Labels.of("struct", "custom label"));
        loki.closeSync();
    }

    @Test
    void logLevelTest() throws InterruptedException {
        TinyLoki loki = TinyLoki.withUrl("http://localhost:3100").withVerboseLogMonitor(true).withBasicAuth("user", "pass").open();
        loki.stream().critical().l("topic", "logLevelTest").open().log("Log level: critical");
        loki.stream().fatal().l("topic", "logLevelTest").open().log("Log level: fatal");
        loki.stream().warning().l("topic", "logLevelTest").open().log("Log level: warning");
        loki.stream().info().l("topic", "logLevelTest").open().log("Log level: info");
        loki.stream().debug().l("topic", "logLevelTest").open().log("Log level: debug");
        loki.stream().verbose().l("topic", "logLevelTest").open().log("Log level: verbose");
        loki.stream().trace().l("topic", "logLevelTest").open().log("Log level: trace");
        loki.stream().unknown().l("topic", "logLevelTest").open().log("Log level: unknown");
        loki.closeSync();
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

        try (TinyLoki loki = TinyLoki.withUrl("http://localhost:3100/loki/api/v1/push").withVerboseLogMonitor(false).withExecutor(new ThreadExecutor(100000)).open()) {

            for (int i = 0; i < 100; ++i) {
                streams.add(loki.stream().l("test_name", "sampleMassiveTest").l("test_run_id", "test_" + testRunId).l(MassiveThread.LABEL_STREAM_IDX, "index_" + i).open());
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
