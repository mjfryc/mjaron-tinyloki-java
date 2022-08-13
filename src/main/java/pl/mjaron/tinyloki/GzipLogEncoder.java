package pl.mjaron.tinyloki;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * Gzip encoder.
 *
 * @see ILogEncoder
 * @since 0.3.4
 */
public class GzipLogEncoder implements ILogEncoder {

    /**
     * Provides name of Gzip content encoding. Consistent with HTTP Content-Encoding header.
     *
     * @return Gzip content encoding HTTP header value.
     * @see ILogEncoder#contentEncoding()
     */
    @Override
    public String contentEncoding() {
        return "gzip";
    }

    /**
     * Encode given bytes to gzip format.
     *
     * @param what Bytes to encode.
     * @return Gzip bytes.
     */
    @Override
    public byte[] encode(byte[] what) {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream(what.length);
        try (final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(bos)) {
            gzipOutputStream.write(what);
        } catch (final IOException e) {
            throw new RuntimeException("Failed to encode to Gzip", e);
        }
        return bos.toByteArray();
    }
}
