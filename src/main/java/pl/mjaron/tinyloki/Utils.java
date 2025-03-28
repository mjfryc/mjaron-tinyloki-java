package pl.mjaron.tinyloki;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Contains universal common methods.
 */
public class Utils {

    /**
     * This class is not instantiable.
     */
    private Utils() {
    }

    /**
     * Provides the stack trace as String.
     *
     * @param throwable The {@link Throwable} object.
     * @return String containing printed stack trace of given {@link Throwable} object.
     * @since 1.0.0
     */
    public static String stackTraceString(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString(); // stack trace as a string
    }

    public static class Nanoseconds {

        public static final long MILLISECOND = 1_000_000;

        public static final long SECOND = 1_000_000_000;

        public static long fromSeconds(long what) {
            return what * SECOND;
        }

        /**
         * Provides current time in nanoseconds.
         *
         * @return Current time in nanoseconds.
         * @since 1.1.3
         */
        public static long currentTime() {
            return System.currentTimeMillis() * MILLISECOND;
        }
    }

    public static long clamp(final long value, final long min, final long max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clampToInt(final long value) {
        return (int) clamp(value, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Source: <a href="https://www.json.org/json-en.html">https://www.json.org/json-en.html</a>
     * Source: <a href="https://www.ietf.org/rfc/rfc4627.txt">https://www.ietf.org/rfc/rfc4627.txt</a>
     * Source: <a href="https://stackoverflow.com/a/69338077/6835932">https://stackoverflow.com/a/69338077/6835932</a>
     *
     * @param b    Reference to exiting {@link StringBuilder} where given <code>text</code> will be appended.
     * @param text {@link CharSequence} which will be escaped and appended to {@link StringBuilder} <code>b</code>.
     */
    public static void escapeJsonString(final StringBuilder b, final CharSequence text) {

        for (int i = 0, length = text.length(); i < length; i++) {
            final char c = text.charAt(i);
            switch (c) {
                case '"': // quotation mark.
                    b.append("\\\"");
                    break;
                case '\\': // reverse solidus.
                    b.append("\\\\");
                    break;
                case '/': // solidus.
                    b.append("\\/");
                    break;
                case '\b': // backspace.
                    b.append("\\b");
                    break;
                case '\f': // formfeed.
                    b.append("\\f");
                    break;
                case '\n': // linefeed.
                    b.append("\\n");
                    break;
                case '\r': // carriage return.
                    b.append("\\r");
                    break;
                case '\t': // horizontal return.
                    b.append("\\t");
                    break;
                default: {
                    if (c > 0x1f) { // 1F is the last control character.
                        b.append(c);
                    } else {
                        // Other control characters [0..1F].
                        b.append("\\u");
                        final String hex = "000" + Integer.toHexString(c);
                        b.append(hex.substring(hex.length() - 4));
                    }
                }
            }
        }
    }

    /**
     * Tells whether given character is valid ASCII capital letter [A-Z]
     *
     * @param ch Character to check.
     * @return True when <code>ch</code> is a capital letter.
     */
    public static boolean isAsciiCapitalLetter(final char ch) {
        return ch >= 0x41 && ch <= 0x5A;
    }

    /**
     * Tells whether given character is valid ASCII lowercase letter [a-z]
     *
     * @param ch Character to check.
     * @return True when <code>ch</code> is a lowercase letter.
     */
    public static boolean isAsciiLowercaseLetter(final char ch) {
        return ch >= 0x61 && ch <= 0x7A;
    }

    /**
     * Tells whether given character is valid ASCII letter [A-Za-z]
     *
     * @param ch Character to check.
     * @return True when <code>ch</code> is a letter.
     */
    public static boolean isAsciiLetter(final char ch) {
        return isAsciiCapitalLetter(ch) || isAsciiLowercaseLetter(ch);
    }

    /**
     * Tells whether given character is valid ASCII digit [0-9]
     *
     * @param ch Character to check.
     * @return True when <code>ch</code> is a digit.
     */
    public static boolean isAsciiDigit(final char ch) {
        return ch >= 0x30 && ch <= 0x39;
    }

    /**
     * Tells whether given character is valid ASCII letter or digit [0-9A-Za-z]
     *
     * @param ch Character to check.
     * @return True when <code>ch</code> is a letter or digit.
     */
    public static boolean isAsciiLetterOrDigit(final char ch) {
        return isAsciiLetter(ch) || isAsciiDigit(ch);
    }

    public static class MonotonicClock {

        /**
         * This class is not instantiable.
         */
        private MonotonicClock() {
        }

        public static long MILLISECONDS_FACTOR = 1_000_000;

        /**
         * Creates the time point pointing to current time.
         *
         * @return Time point which meaning is only valid when used with other {@link MonotonicClock} functions.
         */
        public static long timePoint() {
            return System.nanoTime();
        }

        /**
         * Creates the time point given @param milliseconds from now.
         *
         * @param milliseconds The milliseconds from now where the time point points.
         * @return Time point which meaning is only valid when used with other {@link MonotonicClock} functions.
         */
        public static long timePoint(final long milliseconds) {
            return System.nanoTime() + (milliseconds * MILLISECONDS_FACTOR);
        }

        /**
         * Blocking function. Waits given count of milliseconds.
         *
         * @param object    The object used to wait on it.
         * @param timePoint The time point to wait, created with {@link #timePoint()} method.
         * @return <code>true</code>if waited with success. Given <code>timePoint</code> has passed and cannot wait anymore with given time point. Waiting was not performed.
         * <p>
         * <code>false</code> if failed to wait due to {@link #notify()} or other reason. The wait operation has blocked the thread and cannot determine if waiting is finished.
         * @throws InterruptedException When the calling thread is interrupted.
         */
        public static boolean waitUntil(Object object, final long timePoint) throws InterruptedException {
            final long diff = timePoint - MonotonicClock.timePoint();
            final long diffMilliseconds = diff / MILLISECONDS_FACTOR;
            if (diffMilliseconds > 0) {
                object.wait(diffMilliseconds);
                return false;
            }
            return true;
        }
    }
}
