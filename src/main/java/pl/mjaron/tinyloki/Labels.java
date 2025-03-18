package pl.mjaron.tinyloki;

import java.util.Map;
import java.util.TreeMap;

/**
 * Represents label name - label value mappings. Contains common label constants and its values. Log level constants are
 * defined at:
 * <a href="https://grafana.com/docs/grafana/latest/packages_api/data/loglevel/">Grafana API Reference</a>
 */
@SuppressWarnings("unused")
public class Labels implements Cloneable {

    /**
     * Log level definition.
     */
    public static final String LEVEL = "level";

    /**
     * Fatal log level.
     */
    public static final String FATAL = "critical";

    /**
     * Warning log level.
     */
    public static final String WARN = "warning";

    /**
     * Info log level.
     */
    public static final String INFO = "info";

    /**
     * Debug log level.
     */
    public static final String DEBUG = "debug";

    /**
     * Verbose log level.
     */
    public static final String VERBOSE = "trace";

    /**
     * Trace log level (the same as verbose).
     */
    public static final String TRACE = "trace";

    /**
     * Unknown log level.
     */
    public static final String UNKNOWN = "unknown";
    /**
     * Internal labels container.
     */
    private TreeMap<String, String> map;

    /**
     * Default constructor. Creates empty labels with default parameters.
     */
    public Labels() {
        this.map = new TreeMap<>();
    }

    /**
     * Creates a deep copy of other labels.
     *
     * @param other Other labels instance.
     */
    public Labels(final Labels other) {
        this.map = new TreeMap<>(other.map);
    }

    /**
     * Creates the labels from a given map.
     *
     * @param map Given map is copied to internal Labels map.
     */
    public Labels(final Map<String, String> map) {
        this.map = new TreeMap<>(map);
    }

    /**
     * Verifies if <code>labelIdentifier</code> is not null and not empty.
     *
     * @param labelIdentifier Label name or labelIdentifier value.
     * @throws RuntimeException When label identifier is null or empty.
     * @since 0.2.0
     */
    public static void assertLabelIdentifierNotNullOrEmpty(final String labelIdentifier) {
        if (labelIdentifier == null) {
            throw new RuntimeException("Label identifier is null.");
        }

        if (labelIdentifier.isEmpty()) {
            throw new RuntimeException("Label identifier is empty.");
        }
    }

    /**
     * Checks whether label contains only letters, digits or '_' and first character is letter.
     *
     * @param labelIdentifier Label name or label value to check.
     * @throws RuntimeException when given label identifier is invalid.
     * @since 0.2.0
     * @deprecated This method is unnecessary and will be removed in the future.
     */
    @Deprecated
    public static void validateLabelIdentifierOrThrow(final String labelIdentifier) {
        assertLabelIdentifierNotNullOrEmpty(labelIdentifier);

        final char firstChar = labelIdentifier.charAt(0);
        if (!Character.isLetter(firstChar)) {
            throw new RuntimeException("Cannot validate given label identifier: [" + labelIdentifier + "]:  First character is not a letter: [" + firstChar + "].");
        }

        for (int i = 1; i < labelIdentifier.length(); ++i) {
            final char ch = labelIdentifier.charAt(i);
            if (!Character.isLetterOrDigit(ch) && ch != '_') {
                throw new RuntimeException("Cannot validate given label identifier: [" + labelIdentifier + "]:  Given character is not a letter or digit: [" + ch + "].");
            }
        }
    }

    /**
     * Checks whether label contains only letters, digits or '_' and first character is letter. It doesn't check whether
     * the length of identifier is lower than any length limit.
     *
     * @param labelIdentifier Label name or label value to check.
     * @return True when given label identifier is valid.
     * @since 0.2.0
     * @deprecated Use {@link #checkLabelNameWhenNotEmpty(String)}
     */
    @Deprecated
    public static boolean checkLabelIdentifierWhenNotEmpty(final String labelIdentifier) {
        return checkLabelNameWhenNotEmpty(labelIdentifier);
    }

    /**
     * Checks whether first character is a letter or <code>_</code>. See label naming rules documentation:
     * <a href="https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels">Data model | Prometheus</a>
     *
     * @param firstChar First character of label name.
     * @return True when first character passes label naming rules.
     * @since 0.3.10
     */
    public static boolean isNameFirstCharacterCorrect(final char firstChar) {
        return Utils.isAsciiLetter(firstChar) || firstChar == '_';
    }

    /**
     * Checks whether not-first character is a letter, digit or <code>_</code>. See label naming rules documentation:
     * <a href="https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels">Data model | Prometheus</a>
     *
     * @param notFirstChar Not first character of label name.
     * @return True when first character passes label naming rules.
     * @since 0.3.10
     */
    public static boolean isNameNotFirstCharacterCorrect(final char notFirstChar) {
        return Utils.isAsciiLetterOrDigit(notFirstChar) || notFirstChar == '_';
    }

    /**
     * Checks whether label name begins with <code>__</code>, which is reserved for internal use. See label naming rules
     * documentation:
     * <a href="https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels">Data model | Prometheus</a>
     *
     * @param labelName Full name of a label.
     * @return True when label name is reserved.
     * @since 0.3.10
     */
    public static boolean isNameReservedForInternalUse(final String labelName) {
        return labelName.startsWith("__");
    }

    /**
     * Checks whether label name meets the following rules:
     * <ul>
     * <li>Label contains only letters, digits or <code>_</code></li>
     * <li>First character is letter or <code>_</code></li>
     * <li>Label names beginning with __ are reserved for internal use</li>
     * </ul>
     * <p>
     * See naming rules documentation:
     * <a href="https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels">Data model | Prometheus</a>
     * <p>
     * It doesn't check whether the length of label name is lower than any length limit.
     *
     * @param labelName Label name to check.
     * @return True when given label name is valid.
     * @since 0.3.9
     */
    public static boolean checkLabelNameWhenNotEmpty(final String labelName) {
        if (!isNameFirstCharacterCorrect(labelName.charAt(0))) {
            return false;
        }

        if (isNameReservedForInternalUse(labelName)) {
            return false;
        }

        for (int i = 1; i < labelName.length(); ++i) {
            final char ch = labelName.charAt(i);
            if (!isNameNotFirstCharacterCorrect(ch)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Corrects label name to meet the rules:
     * <ul>
     * <li>Label contains only letters, digits or <code>_</code></li>
     * <li>First character is letter or <code>_</code></li>
     * <li>Label names beginning with __ are reserved for internal use</li>
     * </ul>
     * <p>
     * See naming rules documentation:
     * <a href="https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels">Data model | Prometheus</a>
     *
     * @param labelName Label name to correct.
     * @return Valid label name, accepted by Grafana Loki server.
     */
    public static String correctLabelName(final String labelName) {
        final char[] stringBytes = labelName.toCharArray();

        if (!isNameFirstCharacterCorrect(stringBytes[0]) || isNameReservedForInternalUse(labelName)) {
            stringBytes[0] = 'A';
        }

        for (int i = 1; i < stringBytes.length; ++i) {
            final char ch = stringBytes[i];
            if (!isNameNotFirstCharacterCorrect(ch)) {
                stringBytes[i] = '_';
            }
        }

        return new String(stringBytes);
    }

    /**
     * Replaces invalid characters with `_` character. If first character is invalid, replaces it with `A`. See label
     * naming rules: <a href="https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels">Data model |
     * Prometheus</a>
     * <p>
     * Cuts down label name if it is too long.
     *
     * @param labelName Label name to check.
     * @param maxLength Maximum accepted length of label name.
     * @return Valid label name with wrong symbols removed.
     * @throws RuntimeException when given <code>labelName</code> is null or empty.
     * @since 0.3.9
     */
    public static String prettifyLabelName(final String labelName, final int maxLength) {
        assertLabelIdentifierNotNullOrEmpty(labelName);

        final String validLengthIdentifier = narrowLabelIdentifierLength(labelName, maxLength);

        if (checkLabelNameWhenNotEmpty(validLengthIdentifier)) {
            return validLengthIdentifier; // If identifier is valid, do not clone valid identifier.
        }

        return correctLabelName(validLengthIdentifier);
    }

    /**
     * Cuts down identifier if it is too long.
     *
     * @param labelIdentifier Label name or value to check.
     * @param maxLength       Maximum accepted length of label value.
     * @return Valid label identifier with correct length.
     * @throws RuntimeException when given <code>labelIdentifier</code> is null or empty.
     * @since 0.3.10
     */
    public static String narrowLabelIdentifierLength(final String labelIdentifier, final int maxLength) {
        if (labelIdentifier.length() > maxLength) {
            return labelIdentifier.substring(0, maxLength);
        } else {
            return labelIdentifier;
        }
    }

    /**
     * Cuts down identifier if it is too long. See label naming rules: <a
     * href="https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels">Data model | Prometheus</a>
     *
     * @param labelValue Label value to check.
     * @param maxLength  Maximum accepted length of label value.
     * @return Valid labelValue identifier with correct length.
     * @throws RuntimeException when given <code>labelValue</code> is null or empty.
     * @since 0.3.9
     */
    public static String prettifyLabelValue(final String labelValue, final int maxLength) {
        assertLabelIdentifierNotNullOrEmpty(labelValue);
        return narrowLabelIdentifierLength(labelValue, maxLength);
    }

    /**
     * Replaces invalid characters with `_` character. If first character is invalid, replaces it with `A`.
     * <p>
     * Cuts down identifier if it is too long.
     *
     * @param labelIdentifier Label name or value to check.
     * @param maxLength       Maximum accepted length of identifier.
     * @return Valid labelIdentifier identifier with removed wrong symbols.
     * @throws RuntimeException when given <code>labelIdentifier</code> is null or empty.
     * @since 0.3.0
     * @deprecated Use {@link #prettifyLabelName(String, int)} or {@link #prettifyLabelValue(String, int)} instead.
     */
    @Deprecated
    public static String prettifyLabelIdentifier(final String labelIdentifier, final int maxLength) {
        assertLabelIdentifierNotNullOrEmpty(labelIdentifier);

        final String validLengthIdentifier;
        if (labelIdentifier.length() > maxLength) {
            validLengthIdentifier = labelIdentifier.substring(0, maxLength);
        } else {
            validLengthIdentifier = labelIdentifier;
        }

        if (checkLabelIdentifierWhenNotEmpty(validLengthIdentifier)) {
            return validLengthIdentifier; // If identifier is valid, do not clone valid identifier.
        }

        return correctLabelName(validLengthIdentifier);
    }

    /**
     * Replaces invalid characters with `_` character. If first character is invalid, replaces it with `A`.
     * <p>
     * Doesn't verify max length of identifier.
     *
     * @param labelIdentifier Label name or value to check.
     * @return Valid labelIdentifier identifier with removed wrong symbols.
     * @throws RuntimeException when given <code>labelIdentifier</code> is null or empty.
     * @since 0.2.0
     * @deprecated This method will be removed in the future release. Use {@link #prettifyLabelIdentifier(String, int)}
     * instead.
     */
    @Deprecated
    public static String prettifyLabelIdentifier(final String labelIdentifier) {
        return prettifyLabelIdentifier(labelIdentifier, Integer.MAX_VALUE);
    }

    /**
     * Creates a new {@link Labels} object with corrected label values, so such labels will be accepted by Grafana Loki
     * server.
     *
     * @param labels              Labels to prettify.
     * @param maxLabelNameLength  Max length of valid label name. Based on: <a
     *                            href="https://grafana.com/docs/loki/latest/configuration/">Grafana Loki server
     *                            configuration.</a>
     * @param maxLabelValueLength Max length of valid label value. Based on: <a
     *                            href="https://grafana.com/docs/loki/latest/configuration/">Grafana Loki server
     *                            configuration.</a>
     * @return New {@link Labels} object with corrected label values, so such labels will be accepted by Grafana Loki
     * server.
     * @since 0.3.0
     */
    public static Labels prettify(final Labels labels, final int maxLabelNameLength, final int maxLabelValueLength) {
        return prettify(labels, new LabelSettings(maxLabelNameLength, maxLabelValueLength));
    }

    /**
     * Creates a new {@link Labels} object with corrected label values, so such labels will be accepted by Grafana Loki
     * server.
     * <p>
     * Used by {@link TinyLoki} to pass correct labels to the server.
     *
     * @param labels        Labels to prettify. Label length limits: <a
     *                      href="https://grafana.com/docs/loki/latest/configuration/">Grafana Loki server
     *                      configuration.</a> Label naming rules: <a
     *                      href="https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels">Data model |
     *                      Prometheus</a>
     * @param labelSettings Label parameters.
     * @return New {@link Labels} object with corrected label values, so such labels will be accepted by Grafana Loki
     * server.
     * @since 0.3.0
     */
    public static Labels prettify(final Labels labels, final LabelSettings labelSettings) {
        final Labels prettified = new Labels();
        for (final Map.Entry<String, String> entry : labels.getMap().entrySet()) {
            final String name = prettifyLabelName(entry.getKey(), labelSettings.getMaxLabelNameLength());
            final String value = prettifyLabelValue(entry.getValue(), labelSettings.getMaxLabelValueLength());
            prettified.l(name, value);
        }

        return prettified;
    }

    /**
     * Creates a new instance of {@link Labels} from given {@link Map} object.
     *
     * @param labels Map with label-name mapped to label-value pairs.
     * @return New instance of {@link Labels}.
     * @since 1.0.0
     */
    public static Labels make(final Map<String, String> labels) {
        return new Labels(labels);
    }

    /**
     * Creates a new empty labels instance.
     *
     * @return New instance of {@link Labels}.
     * @since 1.0.0
     */
    public static Labels make() {
        return new Labels();
    }

    /**
     * @return Internal {@link Map} representation of labels content. User should not modify given map because modified
     * values will not be checked for validity.
     * @since 0.1.22
     */
    public Map<String, String> getMap() {
        return map;
    }

    /**
     * Creates a deep copy of this object.
     *
     * @return Deep copy of this object.
     * @throws RuntimeException When any member doesn't implement Cloneable.
     * @since 0.2.2
     */
    @Override
    public Labels clone() {
        try {
            final Labels cloned = (Labels) super.clone();
            cloned.map = new TreeMap<>(this.map);
            return cloned;
        } catch (final CloneNotSupportedException e) {
            throw new RuntimeException("Failed to clone.", e);
        }
    }

    /**
     * Compares this instance with other instance.
     * <p>
     * Two instances are equal when its labels are equal. Label's length limits doesn't matter.
     *
     * @param other Other object instance.
     * @return True if other object is the same as this object.
     * @since 0.2.2
     */
    @Override
    public boolean equals(final Object other) {
        if (other == null) {
            return false;
        }
        if (this == other) {
            return true;
        }
        if (!this.getClass().equals(other.getClass())) {
            return false;
        }
        final Labels otherLabels = (Labels) other;
        return this.map.equals(otherLabels.map);
    }

    /**
     * Provides hash code. See {@link Object#hashCode()}.
     *
     * @return Hash code.
     */
    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    public String toString() {
        return map.toString();
    }

    /**
     * Add a new label and return this object.
     *
     * @param labelName  Label name. Valid label identifier starts with letter and contains only letters, digits or
     *                   '_'.
     * @param labelValue Label value. Valid label identifier starts with letter and contains only letters, digits or
     *                   '_'.
     * @return This object with added label.
     * @throws RuntimeException when given <code>labelName</code> or <code>labelValue</code> is null or empty.
     * @since 0.1.22
     */
    public Labels l(final String labelName, final String labelValue) {
        map.put(labelName, labelValue);
        return this;
    }

    /**
     * Put a map with labels. Valid label identifier starts with letter and contains only letters, digits or '_'.
     *
     * @param map Map containing label key - value pairs.
     * @return This reference.
     * @throws RuntimeException when given <code>map</code> contains null or empty keys or values.
     * @since 0.1.22
     */
    public Labels l(final Map<String, String> map) {
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            this.l(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Put other labels values. New values will override existing values, when label names (keys) are equal.
     *
     * @param other Other Labels instance.
     * @return This reference.
     * @since 0.2.2
     */
    public Labels l(final Labels other) {
        return this.l(other.map);
    }

    /**
     * Sets {@link #LEVEL label level} to {@link #FATAL}, the same as {@link #fatal()}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see #LEVEL
     * @see #FATAL
     * @since 0.2.1
     */
    public Labels critical() {
        return this.l(LEVEL, FATAL);
    }

    /**
     * Sets {@link #LEVEL label level} to {@link #FATAL}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see #LEVEL
     * @see #FATAL
     * @since 0.2.1
     */
    public Labels fatal() {
        return this.l(LEVEL, FATAL);
    }

    /**
     * Sets {@link #LEVEL label level} to {@link #WARN}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see #LEVEL
     * @see #WARN
     * @since 0.2.1
     */
    public Labels warning() {
        return this.l(LEVEL, WARN);
    }

    /**
     * Sets {@link #LEVEL label level} to {@link #INFO}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see #LEVEL
     * @see #INFO
     * @since 0.2.1
     */
    public Labels info() {
        return this.l(LEVEL, INFO);
    }

    /**
     * Sets {@link #LEVEL label level} to {@link #DEBUG}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see #LEVEL
     * @see #DEBUG
     * @since 0.2.1
     */
    public Labels debug() {
        return this.l(LEVEL, DEBUG);
    }

    /**
     * Sets {@link #LEVEL label level} to {@link #VERBOSE}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see #LEVEL
     * @see #VERBOSE
     * @since 0.2.1
     */
    public Labels verbose() {
        return this.l(LEVEL, VERBOSE);
    }

    /**
     * Sets {@link #LEVEL label level} to {@link #TRACE}, the same as {@link #VERBOSE}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see #LEVEL
     * @see #TRACE
     * @see #VERBOSE
     * @since 0.2.1
     */
    public Labels trace() {
        return this.l(LEVEL, TRACE);
    }

    /**
     * Sets {@link #LEVEL label level} to {@link #UNKNOWN}.
     * <p>
     * Only one level may be assigned to single log stream. Setting other level will override previous value.
     *
     * @return This reference.
     * @see #LEVEL
     * @see #UNKNOWN
     * @since 0.2.1
     */
    public Labels unknown() {
        return this.l(LEVEL, UNKNOWN);
    }
}
