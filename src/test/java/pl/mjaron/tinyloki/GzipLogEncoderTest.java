package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GzipLogEncoderTest {

    private static byte[] toByteArray(int[] arr) {
        byte[] bArr = new byte[arr.length];
        for (int i = 0; i < arr.length; ++i) {
            bArr[i] = (byte) arr[i];
        }
        return bArr;
    }

    @Test
    public void test() {
        GzipLogEncoder encoder = new GzipLogEncoder();
        final byte[] encoded = encoder.encode("alphabet".getBytes(StandardCharsets.UTF_8));
        //final int[] expectedInts = new int[]{0x1f, 0x8b, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0a, 0x4b, 0xcc, 0x29, 0xc8, 0x48, 0x4c, 0x4a, 0x2d, 0x01, 0x00, 0x7f, 0xcd, 0x3e, 0x10, 0x08, 0x00, 0x00, 0x00};
        final int[] expectedInts = new int[]{0x1f, 0x8b, 0x08, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x4b, 0xcc, 0x29, 0xc8, 0x48, 0x4c, 0x4a, 0x2d, 0x01, 0x00, 0x7f, 0xcd, 0x3e, 0x10, 0x08, 0x00, 0x00, 0x00};
        final byte[] expected = toByteArray(expectedInts);
        //System.out.println(new BigInteger(1, encoded).toString(16));
        assertArrayEquals(expected, encoded);
        // I don't understand the difference between some tools at tenth byte (with idx 9). Both versions are accepted.
        // 1f 8b 08 00 00 00 00 00 00 0a 4b cc 29 c8 48 4c 4a 2d 01 00 7f cd 3e 10 08 00 00 00
        // 1f 8b 08 00 00 00 00 00 00 00 4b cc 29 c8 48 4c 4a 2d 01 00 7f cd 3e 10 08 00 00 00
    }

    @Test
    void contentEncoding() {
        GzipLogEncoder encoder = new GzipLogEncoder();
        assertEquals("gzip", encoder.contentEncoding());
    }
}
