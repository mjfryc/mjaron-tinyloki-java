package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.logging.Logger;

/**
 * To enable this test, modify the run configuration by adding environment variable:
 * <pre>{@code
 * TINYLOKI_INTEGRATION=1
 * }</pre>
 * <p>
 * Integration test server must be enabled to receive logs, see <code>integration-test-server</code> project directory.
 */
@EnabledIfEnvironmentVariable(named = "TINYLOKI_INTEGRATION", matches = "1")
public class JulIntegrationTest {
// @formatter:off

    @Test
    void julIntegrationTest()throws InterruptedException {
        // Optionally the TinyLoki instance may be gained.
        // TinyLoki loki =

        TinyLokiJulHandler.install(TinyLoki.withUrl("http://localhost:3100")
                .withBasicAuth("user", "pass")
                .withLabels(Labels.of(Labels.SERVICE_NAME, "integration_test")));
        Logger logger = Logger.getLogger("julIntegrationTest");
        logger.info("Hello info.");

        // Optionally, additional logs synchronization may be performed.
        // loki.sync();
    }

// @formatter:on
}
