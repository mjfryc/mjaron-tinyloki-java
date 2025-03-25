package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class TinyLokiJulHandlerTest {

    @Test
    void basicTest() {
        MemoryLogSender memoryLogSender = new MemoryLogSender();

        // @formatter:off
        TinyLokiJulHandler.install(TinyLoki.withUrl("dummy_url")
                .withLogSender(memoryLogSender)
                .withExecutor(new SyncExecutor())
                .withVerboseLogMonitor(true));
        // @formatter:on
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger("julBasicTest");
        logger.info("Hello world.");
        String data = memoryLogSender.getAsString();
        assertTrue(data.contains("Hello world."));
    }
}