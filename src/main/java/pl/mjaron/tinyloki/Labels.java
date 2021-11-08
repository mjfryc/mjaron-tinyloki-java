package pl.mjaron.tinyloki;

import java.util.Map;
import java.util.TreeMap;

/**
 * Represents label name - label value mappings.
 * Contains common label constants and its values.
 * Log level constants are defined at:
 * https://grafana.com/docs/grafana/latest/packages_api/data/loglevel/
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
     * Verifies if labelIdentifier is not null and not empty.
     *
     * @param labelIdentifier Label name or labelIdentifier value.
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
     */
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
     * Checks whether label contains only letters, digits or '_' and first character is letter.
     *
     * @param labelIdentifier Label name or label value to check.
     * @return True when given label identifier is valid.
     * @since 0.2.0
     */
    public static boolean checkLabelIdentifierWhenNotEmpty(final String labelIdentifier) {
        final char firstChar = labelIdentifier.charAt(0);
        if (!Character.isLetter(firstChar)) {
            return false;
        }

        for (int i = 1; i < labelIdentifier.length(); ++i) {
            final char ch = labelIdentifier.charAt(i);
            if (!Character.isLetterOrDigit(ch) && ch != '_') {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether label contains only letters, digits or '_' and first character is letter.
     *
     * @param labelIdentifier Label name or label value to check.
     * @return True when given label identifier is valid.
     * @since 0.2.0
     */
    private static boolean checkLabelIdentifier(final String labelIdentifier) {
        assertLabelIdentifierNotNullOrEmpty(labelIdentifier);
        return checkLabelIdentifierWhenNotEmpty(labelIdentifier);
    }

    /**
     * Replaces invalid characters with `_` character.
     * If first character is invalid, replaces it with `A`.
     *
     * @param labelIdentifier Label name or value to check.
     * @return Valid labelIdentifier identifier with removed wrong symbols.
     * @throws RuntimeException when given <code>labelIdentifier</code> is null or empty.
     * @since 0.2.0
     */
    public static String prettifyLabelIdentifier(final String labelIdentifier) {
        assertLabelIdentifierNotNullOrEmpty(labelIdentifier);
        if (checkLabelIdentifierWhenNotEmpty(labelIdentifier)) { // If identifier is valid, do not clone valid identifier.
            return labelIdentifier;
        }

        char[] stringBytes = labelIdentifier.toCharArray();

        final char firstChar = stringBytes[0];
        if (!Character.isLetter(firstChar)) {
            stringBytes[0] = 'A';
        }

        for (int i = 1; i < stringBytes.length; ++i) {
            final char ch = stringBytes[i];
            if (!Character.isLetterOrDigit(ch)) {
                stringBytes[i] = '_';
            }
        }
        return new String(stringBytes);
    }

    /**
     * Internal labels container.
     */
    private TreeMap<String, String> map;

    /**
     * Default constructor. Creates empty labels.
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
     * Creates labels from a given map.
     *
     * @param map Given map is copied to internal Labels map.
     */
    public Labels(final Map<String, String> map) {
        this.map = new TreeMap<>(map);
    }

    /**
     * @return Internal {@link Map} representation of labels content.
     * User should not modify given map because modified values will not be checked for validity.
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
     *
     * @param other Other object instance.
     * @return True if other object is the same as this object.
     * @since 0.2.2
     */
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
     * Add a new label and return this object.
     *
     * @param labelName  Label name. Valid label identifier starts with letter and contains only letters, digits or '_'.
     * @param labelValue Label value. Valid label identifier starts with letter and contains only letters, digits or '_'.
     * @return This object with added label.
     * @since 0.1.22
     */
    public Labels l(final String labelName, final String labelValue) {
        final String prettifiedName = prettifyLabelIdentifier(labelName);
        final String prettifiedValue = prettifyLabelIdentifier(labelValue);
        map.put(prettifiedName, prettifiedValue);
        return this;
    }

    /**
     * Put a map with labels.
     * Valid label identifier starts with letter and contains only letters, digits or '_'.
     *
     * @param map Map containing label key - value pairs.
     * @return This reference.
     * @since 0.1.22
     */
    public Labels l(final Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            this.l(entry.getKey(), entry.getValue());
        }
        return this;
    }

    /**
     * Put other labels values.
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
