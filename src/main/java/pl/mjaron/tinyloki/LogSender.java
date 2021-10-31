package pl.mjaron.tinyloki;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class LogSender {
    final LogSenderSettings settings;
    URL url;

    public LogSender(final LogSenderSettings settings) {
        this.settings = settings;
        try {
            this.url = new URL(settings.getUrl());
        } catch (final MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize URL with: [" + settings.getUrl() + "].", e);
        }
    }

    public LogSenderSettings getSettings() {
        return settings;
    }

    void send(final byte[] message) {
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
                final String authHeaderEncoded = Base64.getEncoder().encodeToString(authHeaderContentString.getBytes(StandardCharsets.UTF_8));
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
                System.out.println("Unexpected response: " + responseCode + ": " + responseMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to prepare connection.", e);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
