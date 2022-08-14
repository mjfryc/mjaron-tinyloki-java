package pl.mjaron.tinyloki;

/**
 * Responsible for encoding the HTTP content.
 */
public interface ILogEncoder {

    /**
     * HTTP Content-Encoding describing encoding algorithm.
     * See: https://developer.mozilla.org/en-US/docs/Web/HTTP/Headers/Content-Encoding
     *
     * @return HTTP Content-Encoding header value.
     */
    String contentEncoding();

    /**
     * Encodes given bytes in a way consistent with {@link #contentEncoding()} value.
     * @param what Bytes to encode.
     * @return Bytes encoded.
     */
    byte[] encode(final byte[] what);
}
