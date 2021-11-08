package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

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
}