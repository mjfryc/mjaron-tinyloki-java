package pl.mjaron.tinyloki;

import java.util.Map;

/**
 * Helper class for shorter initialization code.
 *
 * call {@link #build()} to initialize {@link ILogStream} with parameters passed to this builder.
 *
 * Example:
 * <pre>{@code
 * ILogStream myStream = logController.stream().info().l("my_custom_label", "value").build();
 * }</pre>
 */
public class StreamBuilder {

    private final LogController logController;
    private final Labels labels = new Labels();

    public StreamBuilder(final LogController logController) {
        this.logController = logController;
    }

    /**
     * Create stream with previously defined labels.
     *
     * @return New {@link ILogStream} instance.
     */
    ILogStream build() {
        return logController.createStream(labels);
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
     * Add a new label and return this object.
     *
     * @param labelName  Label name. Valid label identifier starts with letter and contains only letters, digits or '_'.
     * @param labelValue Label value. Valid label identifier starts with letter and contains only letters, digits or '_'.
     * @return This object with added label.
     * @throws RuntimeException when given <code>labelName</code> or <code>labelValue</code> is null or empty.
     * @since 0.3.2
     */
    public StreamBuilder l(final String labelName, final String labelValue) {
        labels.l(labelName, labelValue);
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