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

    void send(final byte[] message) {
        HttpURLConnection connection = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
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
            final String responseMessage = connection.getResponseMessage();
            System.out.println("Response: " + responseCode + ": " + responseMessage);
            inputStream = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            System.out.println("Response: " + response);
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
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
