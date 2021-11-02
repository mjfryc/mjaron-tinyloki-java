package pl.mjaron.tinyloki;

import java.util.Map;
import java.util.TreeMap;

/**
 * Common label constants and its values.
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
     * @param labelName Label name.
     * @param labelValue Label value.
     * @return This object with added label.
     */
    public Labels l(final String labelName, final String labelValue) {
        map.put(labelName, labelValue);
        return this;
    }
}
