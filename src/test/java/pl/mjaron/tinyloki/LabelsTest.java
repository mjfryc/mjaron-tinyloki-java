package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SpellCheckingInspection")
class LabelsTest {

    @Test
    void lengthLimitTest() {
        final Labels lRef = new Labels();
        lRef.l("qwer", "qwerty");
        final Labels l0 = new Labels();
        l0.l("qwer", "qwerty");
        assertEquals(lRef, Labels.prettify(l0, 4, 6));
        final Labels l1 = new Labels();
        l1.l("qwert", "qwertyu");
        assertEquals(lRef, Labels.prettify(l1, 4, 6));
    }

    @Test
    void assertLabelIdentifierNotNullOrEmpty() {
        assertThrows(RuntimeException.class, () -> Labels.assertLabelIdentifierNotNullOrEmpty(null));
        assertThrows(RuntimeException.class, () -> Labels.assertLabelIdentifierNotNullOrEmpty(""));
        assertDoesNotThrow(() -> Labels.assertLabelIdentifierNotNullOrEmpty(" "));
    }

    @Test
    void validateLabelIdentifierOrThrow() {
        assertThrows(RuntimeException.class, () -> Labels.validateLabelIdentifierOrThrow(""));
        assertDoesNotThrow(() -> Labels.validateLabelIdentifierOrThrow("abc"));
    }

    @Test
    void checkLabelIdentifierWhenNotEmpty() {
        assertTrue(Labels.checkLabelIdentifierWhenNotEmpty("abc"));
    }

    @Test
    void isNameFirstCharacterCorrect() {
        assertTrue(Labels.isNameFirstCharacterCorrect('a'));
        assertTrue(Labels.isNameFirstCharacterCorrect('_'));
        assertFalse(Labels.isNameFirstCharacterCorrect('1'));
    }

    @Test
    void isNameNotFirstCharacterCorrect() {
        assertTrue(Labels.isNameNotFirstCharacterCorrect('a'));
        assertTrue(Labels.isNameNotFirstCharacterCorrect('_'));
        assertTrue(Labels.isNameNotFirstCharacterCorrect('1'));
        assertFalse(Labels.isNameNotFirstCharacterCorrect('&'));
    }

    @Test
    void isNameReservedForInternalUse() {
        assertTrue(Labels.isNameReservedForInternalUse("__"));
        assertTrue(Labels.isNameReservedForInternalUse("___"));
        assertTrue(Labels.isNameReservedForInternalUse("__a"));
        assertFalse(Labels.isNameReservedForInternalUse("_a_"));
        assertFalse(Labels.isNameReservedForInternalUse("a__"));
    }

    @Test
    void checkLabelNameWhenNotEmpty() {
        assertTrue(Labels.checkLabelNameWhenNotEmpty("abc"));
        assertTrue(Labels.checkLabelNameWhenNotEmpty("_abc"));
        assertTrue(Labels.checkLabelNameWhenNotEmpty("_abc"));
        assertTrue(Labels.checkLabelNameWhenNotEmpty("_1abc"));
        assertTrue(Labels.checkLabelNameWhenNotEmpty("_123"));
        assertFalse(Labels.checkLabelNameWhenNotEmpty("123"));
        assertFalse(Labels.checkLabelNameWhenNotEmpty("_12.3"));
        assertFalse(Labels.checkLabelNameWhenNotEmpty("__"));
        assertFalse(Labels.checkLabelNameWhenNotEmpty("__a"));
        assertFalse(Labels.checkLabelNameWhenNotEmpty("1abc"));
        assertFalse(Labels.checkLabelNameWhenNotEmpty(" "));
    }

    @Test
    void correctLabelName() {
        assertEquals("abc", Labels.correctLabelName("abc"));
        assertEquals("A_", Labels.correctLabelName("__"));
        assertEquals("A23", Labels.correctLabelName("123"));
    }

    @Test
    void prettifyLabelName() {
        assertEquals("Aab", Labels.prettifyLabelName("1abc", 3));
        assertEquals("_1ab", Labels.prettifyLabelName("_1abc", 4));
        assertEquals("_1abc", Labels.prettifyLabelName("_1abc", 5));
        assertEquals("_1abc", Labels.prettifyLabelName("_1abc", 6));
    }

    @Test
    void narrowLabelIdentifierLength() {
        assertEquals("ab", Labels.narrowLabelIdentifierLength("abc", 2));
    }

    @Test
    void prettifyLabelValue() {
        assertEquals("1ab", Labels.prettifyLabelValue("1abc", 3));
    }

    @Test
    void prettifyLabelIdentifier() {
        assertEquals("ab", Labels.prettifyLabelIdentifier("abc", 2));
        assertEquals("Ab", Labels.prettifyLabelIdentifier("1bc", 2));
    }

    @Test
    void prettifyLabelIdentifier1() {
        assertEquals("abc", Labels.prettifyLabelIdentifier("abc"));
        assertEquals("Aabc", Labels.prettifyLabelIdentifier("1abc"));
    }

    @Test
    void prettify() {
        LabelSettings labelSettings = new LabelSettings(2, 3);
        Labels l = new Labels().l("1ab", "12345").l("other", "value");
        Labels expected = new Labels().l("Aa", "123").l("ot", "val");
        assertEquals(expected, Labels.prettify(l, labelSettings));
    }

    @Test
    void testPrettify() {
        Labels l = new Labels().l("1ab", "12345").l("other", "value");
        Labels expected = new Labels().l("Aa", "123").l("ot", "val");
        assertEquals(expected, Labels.prettify(l, 2, 3));
    }

    @Test
    void from() {
        HashMap<String, String> map = new HashMap<>();
        map.put("abc", "123");
        map.put("def", "456");
        Labels l = Labels.from(map);
        assertEquals(l.getMap(), map);
    }

    @Test
    void getMap() {
        HashMap<String, String> map = new HashMap<>();
        map.put("abc", "123");
        map.put("def", "456");
        Labels l = Labels.from(map);
        assertEquals(l.getMap(), map);
    }


    @Test
    void testClone() {
        final Labels l0 = new Labels().l("a", "0");
        final Labels l1 = l0.clone();
        assertEquals(l0, l1);
        assertNotEquals(l0.l("b", "1"), l1);
    }

    @Test
    void testEquals() {
        final Labels l0 = new Labels().l("a", "0").l("b", "1");
        final Labels l1 = new Labels().l("b", "1").l("a", "0");
        assertEquals(l0, l1);
        final Labels l2 = new Labels();
        final Labels l3 = new Labels();
        assertEquals(l2, l3);
        assertNotEquals(l0, l2);
        assertEquals(l0, l0.clone().l(l0));
        assertNotEquals(l2, l2.clone().l(l0));
        assertEquals(l2, new Labels(l2));
    }

    @Test
    void testToString() {
        System.out.println(new Labels().l("name", "value").l("a", "b"));
    }

    @Test
    void l() {
        final Labels other = new Labels().l("a", "b");
        final Labels l = new Labels().l(other);
        assertEquals(other, l);
    }

    @Test
    void testL() {
        final Labels other = new Labels().l("a", "b").l("c", "d");
        final Labels l = new Labels().l(other);
        assertEquals(other, l);
    }

    @Test
    void testL1() {
        HashMap<String, String> map = new HashMap<>();
        map.put("abc", "123");
        map.put("def", "456");
        Labels l = new Labels().l(map).l("ghi", "789");
        assertEquals(new Labels().l("abc", "123").l("def", "456").l("ghi", "789"), l);
    }

    @Test
    void critical() {
        assertEquals(Labels.FATAL, new Labels().critical().getMap().get(Labels.LEVEL));
    }

    @Test
    void fatal() {
        assertEquals(Labels.FATAL, new Labels().fatal().getMap().get(Labels.LEVEL));
    }

    @Test
    void warning() {
        assertEquals(Labels.WARN, new Labels().warning().getMap().get(Labels.LEVEL));
    }

    @Test
    void info() {
        assertEquals(Labels.INFO, new Labels().info().getMap().get(Labels.LEVEL));
    }

    @Test
    void debug() {
        assertEquals(Labels.DEBUG, new Labels().debug().getMap().get(Labels.LEVEL));
    }

    @Test
    void verbose() {
        assertEquals(Labels.VERBOSE, new Labels().verbose().getMap().get(Labels.LEVEL));
    }

    @Test
    void trace() {
        assertEquals(Labels.TRACE, new Labels().trace().getMap().get(Labels.LEVEL));
    }

    @Test
    void unknown() {
        assertEquals(Labels.UNKNOWN, new Labels().unknown().getMap().get(Labels.LEVEL));
    }
}