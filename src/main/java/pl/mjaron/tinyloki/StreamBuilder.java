package pl.mjaron.tinyloki;

import java.util.Map;

/**
 * Helper class for shorter initialization code.
 * <p>
 * call {@link #open()} to initialize {@link ILogStream} with parameters passed to this builder.
 * <p>
 * Example:
 * <pre>{@code
 * ILogStream myStream = tinyLoki.stream().info().l("my_custom_label", "value").open();
 * }</pre>
 */
public class StreamBuilder {

    private final TinyLoki tinyLoki;
    private final Labels labels = new Labels();

    public StreamBuilder(final TinyLoki tinyLoki) {
        this.tinyLoki = tinyLoki;
    }

    /**
     * Create stream with previously defined labels.
     *
     * @return New {@link ILogStream} instance.
     */
    public ILogStream open() {
        return tinyLoki.openStream(labels);
    }

    /**
     * Provides access to internal {@link Labels} object.
     *
     * @return Internal {@link Labels} object.
     */
    public Labels getLabels() {
        return labels;
    }

    /**
     * Add a new label and return this object. See the {@link Labels} documentation for restrictions on label names and values.
     *
     * @param name  Label name.
     * @param value Label value.
     * @return This object with added label.
     * @throws RuntimeException when given <code>name</code> or <code>value</code> is null or empty.
     * @since 0.3.2
     */
    public StreamBuilder l(final String name, final String value) {
        labels.l(name, value);
        return this;
    }

    /**
     * Add a new label and return this object. See the {@link Labels} documentation for restrictions on label names and values.
     *
     * @param name  Label name.
     * @param value Label value.
     * @return This object with added label.
     * @throws RuntimeException when given <code>name</code> or <code>value</code> is null or empty.
     * @since 1.1.0
     */
    public StreamBuilder l(final String name, final int value) {
        labels.l(name, value);
        return this;
    }

    /**
     * Add a new label and return this object. See the {@link Labels} documentation for restrictions on label names and values.
     *
     * @param name  Label name.
     * @param value Label value.
     * @return This object with added label.
     * @throws RuntimeException when given <code>name</code> or <code>value</code> is null or empty.
     * @since 1.1.0
     */
    public StreamBuilder l(final String name, final long value) {
        labels.l(name, value);
        return this;
    }

    /**
     * Add a new label and return this object. See the {@link Labels} documentation for restrictions on label names and values.
     *
     * @param name  Label name.
     * @param value Label value.
     * @return This object with added label.
     * @throws RuntimeException when given <code>name</code> or <code>value</code> is null or empty.
     * @since 1.1.0
     */
    public StreamBuilder l(final String name, final char value) {
        labels.l(name, value);
        return this;
    }

    /**
     * Add a new label and return this object. See the {@link Labels} documentation for restrictions on label names and values.
     *
     * @param name  Label name.
     * @param value Label value.
     * @return This object with added label.
     * @throws RuntimeException when given <code>name</code> or <code>value</code> is null or empty.
     * @since 1.1.0
     */
    public StreamBuilder l(final String name, final byte value) {
        labels.l(name, value);
        return this;
    }

    /**
     * Add a new label and return this object. See the {@link Labels} documentation for restrictions on label names and values.
     *
     * @param name  Label name.
     * @param value Label value.
     * @return This object with added label.
     * @throws RuntimeException when given <code>name</code> or <code>value</code> is null or empty.
     * @since 1.1.0
     */
    public StreamBuilder l(final String name, final short value) {
        labels.l(name, value);
        return this;
    }

    /**
     * Add a new label and return this object. See the {@link Labels} documentation for restrictions on label names and values.
     *
     * @param name  Label name.
     * @param value Label value.
     * @return This object with added label.
     * @throws RuntimeException when given <code>name</code> or <code>value</code> is null or empty.
     * @since 1.1.0
     */
    public StreamBuilder l(final String name, final float value) {
        labels.l(name, value);
        return this;
    }

    /**
     * Add a new label and return this object. See the {@link Labels} documentation for restrictions on label names and values.
     *
     * @param name  Label name.
     * @param value Label value.
     * @return This object with added label.
     * @throws RuntimeException when given <code>name</code> or <code>value</code> is null or empty.
     * @since 1.1.0
     */
    public StreamBuilder l(final String name, final double value) {
        labels.l(name, value);
        return this;
    }

    /**
     * Add other labels values. New values will override existing values, when label names (keys) are equal.
     *
     * @param labels any {@link Labels}.
     * @return This reference.
     * @since 0.3.2
     */
    public StreamBuilder l(final Labels labels) {
        this.labels.l(labels);
        return this;
    }

    /**
     * Add other labels values. New values will override existing values, when label names (keys) are equal.
     *
     * @param labels Map with label's name-value mappings.
     * @return This reference.
     * @since 0.3.2
     */
    public StreamBuilder l(final Map<String, String> labels) {
        this.labels.l(labels);
        return this;
    }

    /**
     * Sets {@link Labels#LEVEL label level} to {@link Labels#FATAL}, the same as {@link #fatal()}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see Labels#LEVEL
     * @see Labels#FATAL
     * @since 0.3.2
     */
    public StreamBuilder critical() {
        labels.critical();
        return this;
    }

    /**
     * Sets {@link Labels#LEVEL label level} to {@link Labels#FATAL}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see Labels#LEVEL
     * @see Labels#FATAL
     * @since 0.3.2
     */
    public StreamBuilder fatal() {
        labels.fatal();
        return this;
    }

    /**
     * Sets {@link Labels#LEVEL label level} to {@link Labels#WARN}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see Labels#LEVEL
     * @see Labels#WARN
     * @since 0.3.2
     */
    public StreamBuilder warning() {
        labels.warning();
        return this;
    }

    /**
     * Sets {@link Labels#LEVEL label level} to {@link Labels#INFO}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see Labels#LEVEL
     * @see Labels#INFO
     * @since 0.3.2
     */
    public StreamBuilder info() {
        labels.info();
        return this;
    }

    /**
     * Sets {@link Labels#LEVEL label level} to {@link Labels#DEBUG}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see Labels#LEVEL
     * @see Labels#DEBUG
     * @since 0.3.2
     */
    public StreamBuilder debug() {
        labels.debug();
        return this;
    }

    /**
     * Sets {@link Labels#LEVEL label level} to {@link Labels#VERBOSE}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see Labels#LEVEL
     * @see Labels#VERBOSE
     * @since 0.3.2
     */
    public StreamBuilder verbose() {
        labels.verbose();
        return this;
    }

    /**
     * Sets {@link Labels#LEVEL label level} to {@link Labels#TRACE}, the same as {@link Labels#VERBOSE}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see Labels#LEVEL
     * @see Labels#TRACE
     * @see Labels#VERBOSE
     * @since 0.3.2
     */
    public StreamBuilder trace() {
        labels.trace();
        return this;
    }

    /**
     * Sets {@link Labels#LEVEL label level} to {@link Labels#UNKNOWN}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see Labels#LEVEL
     * @see Labels#UNKNOWN
     * @since 0.3.2
     */
    public StreamBuilder unknown() {
        labels.unknown();
        return this;
    }
}