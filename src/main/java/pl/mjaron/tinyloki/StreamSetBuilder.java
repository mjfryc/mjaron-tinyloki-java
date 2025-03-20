package pl.mjaron.tinyloki;

import java.util.Map;

/**
 * Helper class for instantiating a {@link StreamSet}.
 *
 * @since 1.1.1
 */
public class StreamSetBuilder {

    private final TinyLoki tinyLoki;
    private final Labels labels = new Labels();

    public StreamSetBuilder(final TinyLoki tinyLoki) {
        this.tinyLoki = tinyLoki;
    }

    /**
     * Create stream set with previously defined labels.
     *
     * @return New {@link StreamSet} instance.
     * @since 1.1.1
     */
    public StreamSet open() {
        return new StreamSet(tinyLoki, labels);
    }

    /**
     * Provides access to internal {@link Labels} object.
     *
     * @return Internal {@link Labels} object.
     * @since 1.1.1
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
     * @since 1.1.1
     */
    public StreamSetBuilder l(final String name, final String value) {
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
     * @since 1.1.1
     */
    public StreamSetBuilder l(final String name, final int value) {
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
     * @since 1.1.1
     */
    public StreamSetBuilder l(final String name, final long value) {
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
     * @since 1.1.1
     */
    public StreamSetBuilder l(final String name, final char value) {
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
     * @since 1.1.1
     */
    public StreamSetBuilder l(final String name, final byte value) {
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
     * @since 1.1.1
     */
    public StreamSetBuilder l(final String name, final short value) {
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
     * @since 1.1.1
     */
    public StreamSetBuilder l(final String name, final float value) {
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
     * @since 1.1.1
     */
    public StreamSetBuilder l(final String name, final double value) {
        labels.l(name, value);
        return this;
    }

    /**
     * Add other labels values. New values will override existing values, when label names (keys) are equal.
     *
     * @param labels any {@link Labels}.
     * @return This reference.
     * @since 1.1.1
     */
    public StreamSetBuilder l(final Labels labels) {
        this.labels.l(labels);
        return this;
    }

    /**
     * Add other labels values. New values will override existing values, when label names (keys) are equal.
     *
     * @param labels Map with label's name-value mappings.
     * @return This reference.
     * @since 1.1.1
     */
    public StreamSetBuilder l(final Map<String, String> labels) {
        this.labels.l(labels);
        return this;
    }
}
