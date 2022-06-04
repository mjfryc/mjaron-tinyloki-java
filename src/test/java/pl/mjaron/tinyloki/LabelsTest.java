package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SuppressWarnings("SpellCheckingInspection")
class LabelsTest {

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
    void prettifyLabelIdentifierTest() {
        assertEquals("ab", Labels.prettifyLabelIdentifier("abc", 2));
        assertEquals("Ab", Labels.prettifyLabelIdentifier("1bc", 2));
    }

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
    void toStringTest() {
        System.out.println(new Labels().l("name", "value").l("a", "b"));
    }
}