package pl.mjaron.tinyloki;

public class Utils {

    /**
     * Source: https://stackoverflow.com/a/69338077/6835932
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

    public static void sleep(final long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException("Thread.sleep() has failed.", e);
        }
    }
}
