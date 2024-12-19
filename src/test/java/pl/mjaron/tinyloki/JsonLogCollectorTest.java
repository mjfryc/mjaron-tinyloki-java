package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JsonLogCollectorTest {

    @Test
    void basic() {
        JsonLogCollector collector = new JsonLogCollector();
        collector.setLogListener(ILogListener.dummy());
        JsonLogStream stream = (JsonLogStream) collector.createStream(new Labels().l("abc", "def"));
        assertNotNull(stream.getStringBuilder());
        stream.log(123, "abc");
        final byte[] collected = collector.collect();
        stream.log(123, "abc");
        final String collectedString = collector.collectAsString();
        stream.release();
        assertEquals("application/json", collector.contentType());
    }

    @Test
    void emptyStreams() {
        JsonLogCollector collector = new JsonLogCollector();
        collector.setLogListener(ILogListener.dummy());
        ILogStream stream0 = collector.createStream(new Labels().l("a", "Alpha"));
        ILogStream stream1 = collector.createStream(new Labels().l("b", "Beta"));
        assertNull(collector.collect());
        assertNull(collector.collectAsString());
    }

    @Test
    void twoStreams() {
        JsonLogCollector collector = new JsonLogCollector();
        collector.setLogListener(ILogListener.dummy());
        ILogStream stream0 = collector.createStream(new Labels().l("a", "Alpha"));
        ILogStream stream1 = collector.createStream(new Labels().l("b", "Beta"));
        stream0.log("a_line");
        stream1.log("b_line0");
        stream1.log("b_line1");
        final String collected = collector.collectAsString();
        assertNotNull(collected);
        System.out.println("Collected:\n" + collected);
        assertNull(collector.collectAsString());
    }

    // Used to generate unit test data.
    private static String byteArrayToCode(byte[] array) {
        StringBuilder sb = new StringBuilder(array.length * 2);
        boolean isFirst = true;
        for (int i = 0; i < array.length; ++i) {
            if (!isFirst && i % 8 == 0) {
                sb.append("\n");
            }

            byte b = array[i];
            if (isFirst) {
                isFirst = false;
            } else {
                //sb.append(" ");
                sb.append(", ");
            }
            sb.append("(byte)0x");
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(String hexStr) {
        System.out.println(hexStr);  // log our input make sure it is what we think it is.
        byte[] bArray = new byte[hexStr.length() / 3];
        int index = 0;
        for (String cell : hexStr.split(" ")) {
            byte firstNibble = Byte.parseByte(cell.substring(0, 1), 16); // [x,y)
            byte secondNibble = Byte.parseByte(cell.substring(1, 2), 16);
            byte finalByte = (byte) ((secondNibble) | (firstNibble << 4)); // bit-operations only with numbers, not bytes.
            bArray[index++] = finalByte;
        }
        return bArray;
    }

    @Test
    void specialCharsTest() {
        // See: 🩑

        final String extended = "🩑"; // "\uD83E\uDE51"
        JsonLogCollector collector = new JsonLogCollector();
        collector.setLogListener(ILogListener.dummy());
        ILogStream stream = collector.createStream(new Labels());
        stream.log(0, extended);
        final String logs = collector.collectAsString();
        stream.log(0, extended);
        final byte[] binaryLogs = collector.collect();
        System.out.println("Collected: " + logs);
        // @formatter:off
        final byte[] expected = new byte[] {
                  (byte)0x7b, (byte)0x22, (byte)0x73, (byte)0x74, (byte)0x72, (byte)0x65, (byte)0x61, (byte)0x6d
                , (byte)0x73, (byte)0x22, (byte)0x3a, (byte)0x5b, (byte)0x7b, (byte)0x22, (byte)0x73, (byte)0x74
                , (byte)0x72, (byte)0x65, (byte)0x61, (byte)0x6d, (byte)0x22, (byte)0x3a, (byte)0x7b, (byte)0x7d
                , (byte)0x2c, (byte)0x22, (byte)0x76, (byte)0x61, (byte)0x6c, (byte)0x75, (byte)0x65, (byte)0x73
                , (byte)0x22, (byte)0x3a, (byte)0x5b, (byte)0x5b, (byte)0x22, (byte)0x30, (byte)0x30, (byte)0x30
                , (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x30, (byte)0x22, (byte)0x2c, (byte)0x22, (byte)0xf0
                , (byte)0x9f, (byte)0xa9, (byte)0x91, (byte)0x22, (byte)0x5d, (byte)0x5d, (byte)0x7d, (byte)0x5d
                , (byte)0x7d
        };
        // @formatter:on

        assertArrayEquals(expected, binaryLogs);
        //System.out.println("Collected: " + byteArrayToCode(binaryLogs));
    }
}
