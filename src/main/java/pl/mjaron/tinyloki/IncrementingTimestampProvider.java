package pl.mjaron.tinyloki;

/**
 * Provides uniqueness of logged time for first 999999 logs to the same
 * millisecond with little impact on logging performance.
 *
 * @since 1.1.3
 */
public class IncrementingTimestampProvider implements ITimestampProvider {

    private long lastCurrentTimeMillis = 0;
    private int nanoseconds = 0;

    @Override
    public long next(String message) {
        final long current = System.currentTimeMillis();
        if (current == lastCurrentTimeMillis) {
            if (nanoseconds < 999_999) {
                ++nanoseconds;
            }
            return lastCurrentTimeMillis * 1_000_000 + nanoseconds;
        } else {
            lastCurrentTimeMillis = current;
            nanoseconds = 0;
            return lastCurrentTimeMillis * 1_000_000;
        }
    }

    /**
     * Factory of {@link IncrementingTimestampProvider}.
     */
    public static class Factory implements ITimestampProviderFactory {

        @Override
        public ITimestampProvider create() {
            return new IncrementingTimestampProvider();
        }
    }

    public static ITimestampProviderFactory factory() {
        return new Factory();
    }
}
