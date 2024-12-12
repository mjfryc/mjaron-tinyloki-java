package pl.mjaron.tinyloki;

/**
 * Contains universal common methods.
 */
public class Utils {

    /**
     * Source: <a href="https://stackoverflow.com/a/69338077/6835932">https://stackoverflow.com/a/69338077/6835932</a>
     *
     * @param b    Reference to exiting {@link StringBuilder} where given <code>text</code> will be appended.
     * @param text {@link CharSequence} which will be escaped and appended to {@link StringBuilder} <code>b</code>.
     */
    public static void escapeJsonString(final StringBuilder b, final CharSequence text) {
        for (int i = 0, length = text.length(); i < length; i++) {
            final char c = text.charAt(i);
            switch (c) {
                case '"':
                    b.append("\\\"");
                    break;
                case '\\':
                    b.append("\\\\");
                    break;
                default:
                    if (c > 0x1f) {
                        b.append(c);
                    } else {
                        b.append("\\u");
                        final String hex = "000" + Integer.toHexString(c);
                        b.append(hex.substring(hex.length() - 4));
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
         * @param timePoint The timepoint to wait, created with {@link #timePoint()} method.
         * @return <code>true</code> if waited with success.
         * <p/>
         * <code>false</code> if failed to wait due to {@link #notify()} or other reason.
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
