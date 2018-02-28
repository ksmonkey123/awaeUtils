package ch.awae.utils.collection.immutable;

import org.junit.Test;
import static org.junit.Assert.*;

public class ListTest {

    @Test
    public void testToString() {
        List<String> list = List.empty();
        list = list.prepend("hi there");
        list = list.prepend("head?");
        String s = list.toString();
        assertEquals("List(head?,hi there)", s);
    }

    @Test
    public void testSize() {
        List<String> list = List.of("1", "2", "3");
        assertEquals(3, list.size());
    }

    @Test
    public void testCreationFromArray() {
        List<String> reference = List.<String>empty().prepend("3").prepend("2").prepend("1");
        List<String> varArgs = List.of("1", "2", "3");

        assertEquals(reference, varArgs);
    }

    @Test
    public void testExtractionIntoBareArray() {
        List<String> list = List.of("1", "2", "3");
        Object[] array = list.toArray();
        assertEquals(3, array.length);
        assertArrayEquals(new Object[] { "1", "2", "3" }, array);
    }

    @Test
    public void testExtractionIntoStringArray() {
        List<String> list = List.of("1", "2", "3");
        String[] array = list.toArray(new String[0]);
        assertEquals(3, array.length);
        assertArrayEquals(new Object[] { "1", "2", "3" }, array);

        String[] longArray = new String[4];
        String[] received = list.toArray(longArray);
        assertTrue("array filled", longArray == received);
        assertArrayEquals(new Object[] { "1", "2", "3", null }, received);
    }

    @Test
    public void testListNavigators() {
        List<String> list0 = List.of("1", "2");
        List<String> list1 = list0.prepend("3");
        List<String> list2 = list0.prepend("x");

        assertTrue("shared tail", list1.tail() == list2.tail());
        assertEquals("3", list1.head());
        assertEquals("x", list2.get(0));
        assertEquals("1", list2.get(1));
        assertEquals("2", list2.get(2));
        assertEquals("2", list0.get(1));

        assertEquals(List.empty(), list0.tail().tail());
    }

}
