package pl.mjaron.tinyloki;

/**
 * Contains universal common methods.
 */
public class Utils {

    /**
     * Source: https://stackoverflow.com/a/69338077/6835932
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
     * Sleeps given amount of time. Allows calling without try-catch block.
     *
     * @param milliseconds Milliseconds count.
     * @throws RuntimeException When any thread has interrupted this thread.
     */
    @SuppressWarnings("unused")
    public static void sleep(final long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException("Thread.sleep() has failed.", e);
        }
    }
}
