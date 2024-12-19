package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LogSenderSettingsTest {

    @Test
    void basic() {
        final LogSenderSettings s = new LogSenderSettings();
        s.setUrl("http://example.com").setUser("User").setPassword("pwd").setContentType("content_type").setContentEncoding("content_encoding").setConnectTimeout(15);
        assertEquals("http://example.com", s.getUrl());
        assertEquals("User", s.getUser());
        assertEquals("pwd", s.getPassword());
        assertEquals("content_type", s.getContentType());
        assertEquals("content_encoding", s.getContentEncoding());
        assertEquals(15, s.getConnectTimeout());
    }
}
