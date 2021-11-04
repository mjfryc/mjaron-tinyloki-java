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
public class Labels {

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
    private final Map<String, String> map = new TreeMap<>();

    /**
     * @return Map representation of labels content.
     */
    public Map<String, String> getMap() {
        return map;
    }

    /**
     * Add a new label and return this object.
     *
     * @param labelName  Label name. Valid label identifier starts with letter and contains only letters, digits or '_'.
     * @param labelValue Label value. Valid label identifier starts with letter and contains only letters, digits or '_'.
     * @return This object with added label.
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
     */
    public Labels l(final Map<String, String> map) {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            this.l(entry.getKey(), entry.getValue());
        }
        return this;
    }
}
