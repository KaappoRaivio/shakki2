package misc;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class SplitterTest {

    @Test
    public void testSplitting() {
        List<String> list = List.of("a", "b", "c", "d", "e", "f", "g", "h", "i");

        var result = Splitter.splitListInto(list, 3);
        System.out.println(result);
        assertEquals("[[a, b, c], [d, e, f], [g, h, i]]", result.toString());
        result = Splitter.splitListInto(list, 4);
        assertEquals("[[a, b, c], [d, e], [f, g], [h, i]]", result.toString());

    }
}