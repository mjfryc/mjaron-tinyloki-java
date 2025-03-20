package pl.mjaron.tinyloki;

/**
 * Defines parameters of label name and value.
 * These parameters allow preparing values accepted by Grafana Loki server.
 * <p>
 * Based on: <a href="https://grafana.com/docs/loki/latest/configuration/">Grafana Loki limits_config</a>
 *
 * @see #DEFAULT_MAX_LABEL_NAME_LENGTH
 * @see #DEFAULT_MAX_LABEL_VALUE_LENGTH
 * @since 0.3.0
 */
public class LabelSettings {

    /**
     * Determines safe length of label name, based on default configuration of loki server at <c>limits_config</c>:
     * <pre>{@code
     * [max_label_name_length: <int> | default = 1024]
     * }</pre>
     * See: <a href="https://grafana.com/docs/loki/latest/configuration/">Grafana Loki limits_config</a>
     *
     * @since 0.3.0
     */
    public static final int DEFAULT_MAX_LABEL_NAME_LENGTH = 1024;

    /**
     * Determines safe length of label value, based on default configuration of loki server at <c>limits_config</c>:
     * <pre>{@code
     * [max_label_value_length: <int> | default = 2048]
     * }</pre>
     * See: <a href="https://grafana.com/docs/loki/latest/configuration/">Grafana Loki limits_config</a>
     *
     * @since 0.3.0
     */
    public static final int DEFAULT_MAX_LABEL_VALUE_LENGTH = 2048;

    /**
     * Determines accepted length of added label name, based on default configuration of loki server.
     * See: <a href="https://grafana.com/docs/loki/latest/configuration/">Grafana Loki limits_config</a>
     *
     * @see #DEFAULT_MAX_LABEL_NAME_LENGTH
     * @since 0.3.0
     */
    private int maxLabelNameLength = DEFAULT_MAX_LABEL_NAME_LENGTH;

    /**
     * Determines accepted length of added label value, based on default configuration of loki server.
     * See: <a href="https://grafana.com/docs/loki/latest/configuration/">Grafana Loki limits_config</a>
     *
     * @see #DEFAULT_MAX_LABEL_VALUE_LENGTH
     * @since 0.3.0
     */
    private int maxLabelValueLength = DEFAULT_MAX_LABEL_VALUE_LENGTH;

    /**
     * Initializes {@link LabelSettings label settings} with custom values.
     *
     * @param maxLabelNameLength  Determines safe length of label value, based on default configuration of loki server.
     *                            Based on: <a href="https://grafana.com/docs/loki/latest/configuration/">Grafana Loki limits_config</a>
     * @param maxLabelValueLength Determines accepted length of added label value, based on default configuration of loki server.
     *                            Based on: <a href="https://grafana.com/docs/loki/latest/configuration/">Grafana Loki limits_config</a>
     */
    public LabelSettings(final int maxLabelNameLength, final int maxLabelValueLength) {
        this.maxLabelNameLength = maxLabelNameLength;
        this.maxLabelValueLength = maxLabelValueLength;
    }

    /**
     * Default constructor. Initializes {@link LabelSettings label settings} with server default values.
     * Based on: <a href="https://grafana.com/docs/loki/latest/configuration/">Grafana Loki limits_config</a>
     */
    public LabelSettings() {
    }

    /**
     * Gives information about maximum allowed length of label name.
     *
     * @return Maximum length of valid label name.
     * @since 0.3.0
     */
    public int getMaxLabelNameLength() {
        return maxLabelNameLength;
    }

    /**
     * Sets maximum allowed length of label name.
     *
     * @param maxLabelNameLength Maximum allowed length of label name.
     * @since 0.3.0
     */
    public void setMaxLabelNameLength(int maxLabelNameLength) {
        this.maxLabelNameLength = maxLabelNameLength;
    }

    /**
     * Gives information about maximum allowed length of label value.
     *
     * @return Maximum length of valid label name.
     * @since 0.3.0
     */
    public int getMaxLabelValueLength() {
        return maxLabelValueLength;
    }

    /**
     * Sets maximum allowed length of label value.
     *
     * @param maxLabelValueLength Maximum allowed length of label value.
     * @since 0.3.0
     */
    public void setMaxLabelValueLength(int maxLabelValueLength) {
        this.maxLabelValueLength = maxLabelValueLength;
    }
}