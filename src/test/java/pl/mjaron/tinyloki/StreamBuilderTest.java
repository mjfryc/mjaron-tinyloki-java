package pl.mjaron.tinyloki;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StreamBuilderTest {

    @Test
    void l() {
        StreamBuilder streamBuilder = new StreamBuilder(null).l(new Labels().info());
        assertEquals(new Labels().l(Labels.LEVEL, Labels.INFO), streamBuilder.getLabels());
    }

    @Test
    void testL() {
        Map<String, String> map = new HashMap<>();
        map.put("d", "D");
        map.put("b", "B");
        map.put("_", "aaa");
        StreamBuilder streamBuilder = new StreamBuilder(null).l(map);

        Map<String, String> expected = new HashMap<>();
        expected.put("d", "D");
        expected.put("b", "B");
        expected.put("_", "aaa");
        assertEquals(expected, streamBuilder.getLabels().getMap());
    }

    @Test
    void testL1() {
        final Labels labels = new Labels().l("d", "D").l("a", "aaa");
        StreamBuilder streamBuilder = new StreamBuilder(null).fatal().l(labels);

        Map<String, String> expected = new HashMap<>();
        expected.put("d", "D");
        expected.put("a", "aaa");
        expected.put(Labels.LEVEL, Labels.FATAL);
        assertEquals(expected, streamBuilder.getLabels().getMap());
    }

    @Test
    void critical() {
        assertEquals(new Labels().critical(), new StreamBuilder(null).critical().getLabels());
    }

    @Test
    void fatal() {
        assertEquals(new Labels().fatal(), new StreamBuilder(null).fatal().getLabels());
    }

    @Test
    void warning() {
        assertEquals(new Labels().warning(), new StreamBuilder(null).warning().getLabels());
    }

    @Test
    void info() {
        assertEquals(new Labels().info(), new StreamBuilder(null).info().getLabels());
    }

    @Test
    void debug() {
        assertEquals(new Labels().debug(), new StreamBuilder(null).debug().getLabels());
    }

    @Test
    void verbose() {
        assertEquals(new Labels().verbose(), new StreamBuilder(null).verbose().getLabels());
    }

    @Test
    void trace() {
        assertEquals(new Labels().trace(), new StreamBuilder(null).trace().getLabels());
    }

    @Test
    void unknown() {
        assertEquals(new Labels().unknown(), new StreamBuilder(null).unknown().getLabels());
    }
}