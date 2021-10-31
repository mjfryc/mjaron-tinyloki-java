package pl.mjaron.tinyloki;

import pl.mjaron.tinyloki.third_party.Base64Coder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
//import java.util.Base64;

public class LogSender {
    final LogSenderSettings settings;
    private ILogMonitor logMonitor = null;
    private URL url;

    public LogSender(final LogSenderSettings settings) {
        this.settings = settings;
        try {
            this.url = new URL(settings.getUrl());
        } catch (final MalformedURLException e) {
            throw new RuntimeException("Failed to initialize URL with: [" + settings.getUrl() + "].", e);
        }
    }

    public LogSenderSettings getSettings() {
        return settings;
    }

    public ILogMonitor getLogMonitor() {
        return logMonitor;
    }

    public void setLogMonitor(ILogMonitor logMonitor) {
        this.logMonitor = logMonitor;
    }

    void send(final byte[] message) {
        logMonitor.send(message);
        HttpURLConnection connection = null;
        OutputStream outputStream = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("connection", "close");
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Content-Type", settings.getContentType());
            connection.setRequestProperty("Content-Length", Integer.toString(message.length));
            //connection.setRequestProperty("Content-Language", "en-US");

            if (settings.getUser() != null && settings.getPassword() != null) {
                final String authHeaderContentString = settings.getUser() + ":" + settings.getPassword();
                //final byte[] authHeaderBytes = authHeaderContentString.getBytes(StandardCharsets.UTF_8);
                //Base64.getEncoder().encodeToString();
                final String authHeaderEncoded = Base64Coder.encodeString(authHeaderContentString);
                connection.setRequestProperty("Authorization", "Basic " + authHeaderEncoded);
            }

            connection.setAllowUserInteraction(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            outputStream = connection.getOutputStream();
            outputStream.write(message);
            outputStream.close();
            outputStream = null;
            final int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_NO_CONTENT) {
                final String responseMessage = connection.getResponseMessage();
                logMonitor.sendErr(responseCode, responseMessage);
            }
            else {
                logMonitor.sendOk(responseCode);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to prepare connection.", e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logMonitor.onException(e);
                }
            }
        }
    }
}
