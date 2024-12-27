package pl.mjaron.tinyloki;

import pl.mjaron.tinyloki.third_party.Base64Coder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * Implementation of sending bytes to HTTP server.
 *
 * @since 0.3.0
 */
public class HttpLogSender implements ILogSender {
    private LogSenderSettings settings;
    private ILogMonitor logMonitor;
    private URL url;

    @Override
    public void configure(LogSenderSettings logSenderSettings, ILogMonitor logMonitor) {
        this.settings = logSenderSettings;
        this.logMonitor = logMonitor;
        try {
            this.url = new URL(settings.getUrl());
        } catch (final MalformedURLException e) {
            throw new RuntimeException("Failed to initialize URL with: [" + settings.getUrl() + "].", e);
        }
    }

    private static InputStream tryGetInputStream(HttpURLConnection connection) {
        try {
            return connection.getInputStream();
        } catch (final IOException ignored) {
            return null;
        }
    }

    private static InputStream tryGetErrorStream(HttpURLConnection connection) {
        return connection.getErrorStream();
    }

    private String tryStreamToString(final InputStream stream) {
        if (stream == null) {
            return "";
        }
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream))) {
            return bufferedReader.lines().collect(Collectors.joining("\n"));
        } catch (final IOException ignored) {
            return "";
        }
    }

    private void tryConsumeStream(final InputStream stream) {
        if (stream == null) {
            return;
        }
        try {
            while (true) {
                if (stream.read() == -1) {
                    break;
                }
            }
        } catch (final IOException ignored) {
        } finally {
            try {
                stream.close();
            } catch (final IOException ignored) {
            }
        }
    }

    /**
     * Creates connection and sends given data by HTTP request.
     * Calls several {@link ILogMonitor} methods pointing what's the request data and HTTP response result.
     *
     * @param message Data to send in HTTP request content.
     * @throws RuntimeException On connection error.
     */
    public void send(final byte[] message) throws IOException {
        logMonitor.send(message);
        HttpURLConnection connection = null;
        int responseCode = -1;
        boolean isError = true;
        try {
            // Based on: https://docs.oracle.com/javase/7/docs/technotes/guides/net/http-keepalive.html
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(settings.getConnectTimeout());
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", settings.getContentType());
            if (settings.getContentEncoding() != null) {
                connection.setRequestProperty("Content-Encoding", settings.getContentEncoding());
            }
            connection.setRequestProperty("Content-Length", Integer.toString(message.length));

            if (settings.getUser() != null && settings.getPassword() != null) {
                final String authHeaderContentString = settings.getUser() + ":" + settings.getPassword();
                final String authHeaderEncoded = Base64Coder.encodeString(authHeaderContentString);
                connection.setRequestProperty("Authorization", "Basic " + authHeaderEncoded);
            }

            try (final OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(message);
            }

            responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
                logMonitor.sendOk(responseCode);
                isError = false;
            }
        } finally {
            if (connection != null) {
                if (isError) {
                    final String responseString = tryStreamToString(tryGetInputStream(connection));
                    final String errorString = tryStreamToString(tryGetErrorStream(connection));
                    String errorDescription = "HTTP response code: [" + responseCode + "], response: [" + responseString + "], error: [" + errorString + "].";
                    logMonitor.sendErr(responseCode, errorDescription);
                } else {
                    tryConsumeStream(tryGetInputStream(connection));
                    tryConsumeStream(tryGetErrorStream(connection));
                }
            }
        }
    }
}
