package pl.mjaron.tinyloki;

/**
 * Basic implementation.
 * <p>
 * Provides current timestamp provided by {@link System#currentTimeMillis()}
 * and converted to nanoseconds by multiplying by <code>1_000_000</code>.
 *
 * @since 1.1.3
 */
public class CurrentTimestampProvider implements ITimestampProvider {

    @Override
    public long next(String message) {
        return Utils.Nanoseconds.currentTime();
    }

    /**
     * Factory of {@link CurrentTimestampProvider}.
     */
    public static class Factory implements ITimestampProviderFactory {

        @Override
        public ITimestampProvider create() {
            return new CurrentTimestampProvider();
        }
    }

    public static ITimestampProviderFactory factory() {
        return new Factory();
    }
}
