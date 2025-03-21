package pl.mjaron.tinyloki;

/**
 * Creates new instance of {@link ITimestampProvider} implementation.
 *
 * @since 1.1.3
 */
public interface ITimestampProviderFactory {

    /**
     * Creates new instance of {@link ITimestampProvider} implementation.
     *
     * @return New instance of {@link ITimestampProvider} implementation.
     * @since 1.1.3
     */
    ITimestampProvider create();

    /**
     * Provides given <code>factory</code> or the {@link #getDefault() default} implementation if parameter is <code>null</code>.
     *
     * @param factory The original factory or <code>null</code> value.
     * @return If not a <code>null</code>, the <code>factory</code> parameter, otherwise the {@link #getDefault() default} implementation.
     * @since 1.1.3
     */
    static ITimestampProviderFactory orDefault(ITimestampProviderFactory factory) {
        if (factory == null) {
            return getDefault();
        }
        return factory;
    }

    /**
     * @return New factory of {@link CurrentTimestampProvider}.
     */
    static ITimestampProviderFactory current() {
        return CurrentTimestampProvider.factory();
    }

    /**
     * @return New factory of {@link IncrementingTimestampProvider}.
     */
    static ITimestampProviderFactory incrementing() {
        return IncrementingTimestampProvider.factory();
    }

    /**
     * @return {@link CurrentTimestampProvider}.
     */
    static ITimestampProviderFactory getDefault() {
        return current();
    }
}
