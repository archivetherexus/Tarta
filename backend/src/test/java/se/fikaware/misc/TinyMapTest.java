package se.fikaware.misc;

import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class TinyMapTest {

    @Test
    public void testPutGet() {
        TinyMap<String, String> map = new TinyMap<>();

        map.put("a", "1");
        map.put("b", "2");
        map.put("c", "3");

        Assert.assertEquals("1", map.get("a"));
        Assert.assertEquals("2", map.get("b"));
        Assert.assertEquals("3", map.get("c"));
    }

    @Test
    public void testPutGetClear() {
        TinyMap<String, String> map = new TinyMap<>();

        map.put("a", "1");
        map.put("b", "2");
        map.put("c", "3");

        Assert.assertEquals("1", map.get("a"));
        Assert.assertEquals("2", map.get("b"));
        Assert.assertEquals("3", map.get("c"));

        map.clear();

        map.put("a", "one");
        map.put("b", "two");
        map.put("c", "three");


        Assert.assertNotEquals("1", map.get("a"));
        Assert.assertNotEquals("2", map.get("b"));
        Assert.assertNotEquals("3", map.get("c"));

        Assert.assertEquals("one", map.get("a"));
        Assert.assertEquals("two", map.get("b"));
        Assert.assertEquals("three", map.get("c"));
    }

    @Test
    public void testPutGetClearSize() {
        TinyMap<String, String> map = new TinyMap<>();

        Assert.assertEquals(0, map.size());

        map.put("a", "1");
        map.put("b", "2");
        map.put("c", "3");

        Assert.assertEquals("1", map.get("a"));
        Assert.assertEquals("2", map.get("b"));
        Assert.assertEquals("3", map.get("c"));

        Assert.assertEquals(3, map.size());

        map.clear();

        Assert.assertEquals(0, map.size());

        map.put("a", "one");
        map.put("b", "two");
        map.put("c", "three");

        Assert.assertEquals(3, map.size());

        Assert.assertNotEquals("1", map.get("a"));
        Assert.assertNotEquals("2", map.get("b"));
        Assert.assertNotEquals("3", map.get("c"));

        Assert.assertEquals("one", map.get("a"));
        Assert.assertEquals("two", map.get("b"));
        Assert.assertEquals("three", map.get("c"));
    }

    @Test
    public void testEntrySetIterator() {
        TinyMap<String, String> map = new TinyMap<>();

        map.put("a", "1");
        map.put("b", "2");
        map.put("c", "3");

        var iterator = map.entrySet().iterator();

        Map.Entry<String, String> e;

        Assert.assertTrue(iterator.hasNext());
        e = iterator.next();
        Assert.assertEquals("c", e.getKey());
        Assert.assertEquals("3", e.getValue());

        Assert.assertTrue(iterator.hasNext());
        e = iterator.next();
        Assert.assertEquals("b", e.getKey());
        Assert.assertEquals("2", e.getValue());

        Assert.assertTrue(iterator.hasNext());
        e = iterator.next();
        Assert.assertEquals("a", e.getKey());
        Assert.assertEquals("1", e.getValue());
    }

    @Test
    public void testValuesIterator() {
        TinyMap<String, String> map = new TinyMap<>();

        map.put("a", "1");
        map.put("b", "2");
        map.put("c", "3");

        var iterator = map.values().iterator();

        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("3", iterator.next());

        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("2", iterator.next());

        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("1", iterator.next());
    }

    @Test
    public void testKeySetIterator() {
        TinyMap<String, String> map = new TinyMap<>();

        map.put("a", "1");
        map.put("b", "2");
        map.put("c", "3");

        var iterator = map.keySet().iterator();

        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("c", iterator.next());

        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("b", iterator.next());

        Assert.assertTrue(iterator.hasNext());
        Assert.assertEquals("a", iterator.next());
    }

    @Test
    public void testIsEmpty() {
        TinyMap<String, String> map = new TinyMap<>();

        Assert.assertTrue(map.isEmpty());

        map.put("a", "1");
        map.put("b", "2");

        Assert.assertFalse(map.isEmpty());

        map.clear();

        Assert.assertTrue(map.isEmpty());

        map.put("a", "1");

        Assert.assertFalse(map.isEmpty());
    }

    @Test
    public void testPutAll() {
        TinyMap<String, String> map = new TinyMap<>();

        TinyMap<String, String> other = new TinyMap<>();

        other.put("a", "1");
        other.put("b", "2");
        other.put("c", "3");

        map.putAll(other);

        map.put("d", "4");

        var iterator = map.entrySet().iterator();
        Map.Entry<String, String> entry;

        Assert.assertTrue(iterator.hasNext());
        entry = iterator.next();
        Assert.assertEquals("d", entry.getKey());
        Assert.assertEquals("4", entry.getValue());

        Assert.assertTrue(iterator.hasNext());
        entry = iterator.next();
        Assert.assertEquals("a", entry.getKey());
        Assert.assertEquals("1", entry.getValue());

        Assert.assertTrue(iterator.hasNext());
        entry = iterator.next();
        Assert.assertEquals("b", entry.getKey());
        Assert.assertEquals("2", entry.getValue());

        Assert.assertTrue(iterator.hasNext());
        entry = iterator.next();
        Assert.assertEquals("c", entry.getKey());
        Assert.assertEquals("3", entry.getValue());
    }

    @Test
    public void testRemove() {
        TinyMap<String, String> map = new TinyMap<>();

        map.put("a", "1");
        map.put("b", "2");

        var iterator = map.entrySet().iterator();
        map.remove("a");

        Assert.assertTrue(iterator.hasNext());
        iterator.next();
        Assert.assertFalse(iterator.hasNext());
    }

    public void testForEach1() {
        TinyMap<String, String> map = new TinyMap<>();
        map.put("hello", "world");

        map.forEach((k, v) -> {
            Assert.assertEquals(k, "hello");
            Assert.assertEquals(v, "world");
        });
    }

    public void testForEach2() {
        TinyMap<String, String> map = new TinyMap<>();
        map.put("key1", "value");
        map.put("key2", "value");
        map.put("key3", "value");

        map.forEach((k, v) -> {
            Assert.assertEquals(v, "value");
        });
    }
}
