package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {

    String escapeJsonStringHelper(final String what) {
        StringBuilder b = new StringBuilder();
        Utils.escapeJsonString(b, what);
        return b.toString();
    }

    @Test
    void escapeJsonString() {
        assertEquals("abc", escapeJsonStringHelper("abc"));
        assertEquals("\\\"", escapeJsonStringHelper("\"")); // "
        assertEquals("\\\\", escapeJsonStringHelper("\\")); // \
        assertEquals("\\/", escapeJsonStringHelper("/")); // /
        assertEquals("\\b", escapeJsonStringHelper("\b")); //
        assertEquals("\\f", escapeJsonStringHelper("\f")); //
        assertEquals("\\b", escapeJsonStringHelper("\b")); //
        assertEquals("\\n", escapeJsonStringHelper("\n")); //
        assertEquals("\\r", escapeJsonStringHelper("\r")); //
        assertEquals("\\t", escapeJsonStringHelper("\t")); //
        assertEquals("\\u0001", escapeJsonStringHelper("\u0001"));
        assertEquals("\\u0000", escapeJsonStringHelper("\u0000"));
    }

    @Test
    void isAsciiCapitalLetter() {
        assertTrue(Utils.isAsciiCapitalLetter('A'));
        assertTrue(Utils.isAsciiCapitalLetter('M'));
        assertTrue(Utils.isAsciiCapitalLetter('Z'));
        assertFalse(Utils.isAsciiCapitalLetter('@'));
        assertFalse(Utils.isAsciiCapitalLetter('['));
        assertFalse(Utils.isAsciiCapitalLetter('a'));
        assertFalse(Utils.isAsciiCapitalLetter('z'));
    }

    @Test
    void isAsciiLowercaseLetter() {
        assertTrue(Utils.isAsciiLowercaseLetter('a'));
        assertTrue(Utils.isAsciiLowercaseLetter('m'));
        assertTrue(Utils.isAsciiLowercaseLetter('z'));
        assertFalse(Utils.isAsciiLowercaseLetter('`'));
        assertFalse(Utils.isAsciiLowercaseLetter('{'));
        assertFalse(Utils.isAsciiLowercaseLetter('A'));
        assertFalse(Utils.isAsciiLowercaseLetter('Z'));
    }

    @Test
    void isAsciiLetter() {
        assertTrue(Utils.isAsciiLetter('a'));
        assertTrue(Utils.isAsciiLetter('A'));
        assertTrue(Utils.isAsciiLetter('b'));
        assertTrue(Utils.isAsciiLetter('B'));
        assertTrue(Utils.isAsciiLetter('y'));
        assertTrue(Utils.isAsciiLetter('Y'));
        assertTrue(Utils.isAsciiLetter('z'));
        assertTrue(Utils.isAsciiLetter('Z'));
        assertFalse(Utils.isAsciiLetter('1'));
        assertFalse(Utils.isAsciiLetter('5'));
        assertFalse(Utils.isAsciiLetter('0'));
        assertFalse(Utils.isAsciiLetter('$'));
    }

    @Test
    void isAsciiDigit() {
        assertTrue(Utils.isAsciiDigit('0'));
        assertTrue(Utils.isAsciiDigit('1'));
        assertTrue(Utils.isAsciiDigit('9'));
        assertFalse(Utils.isAsciiDigit('A'));
        assertFalse(Utils.isAsciiDigit('F'));
        assertFalse(Utils.isAsciiDigit('a'));
        assertFalse(Utils.isAsciiDigit('f'));
        assertFalse(Utils.isAsciiDigit('/'));
        assertFalse(Utils.isAsciiDigit(':'));
    }

    @Test
    void isAsciiLetterOrDigit() {
        assertTrue(Utils.isAsciiLetterOrDigit('a'));
        assertTrue(Utils.isAsciiLetterOrDigit('A'));
        assertTrue(Utils.isAsciiLetterOrDigit('0'));
        assertTrue(Utils.isAsciiLetterOrDigit('9'));
        assertFalse(Utils.isAsciiLetterOrDigit('?'));
    }
}