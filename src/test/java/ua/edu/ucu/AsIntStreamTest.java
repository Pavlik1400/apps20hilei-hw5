package ua.edu.ucu;


import ua.edu.ucu.function.IntConsumer;
import ua.edu.ucu.function.IntToIntStreamFunction;
import ua.edu.ucu.stream.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

import java.util.Arrays;


public class AsIntStreamTest {
    private IntStream stream;
    private final double DELTA = 0.0001;

    @Before
    public void init() {
        stream = AsIntStream.of(-2, -1, 0, 1, 2, 9, 5, 3, 76, 121, -121);
    }

    @Test
    public void testOfToArray() {
        assertArrayEquals(stream.toArray(), new int[] {
                -2, -1, 0, 1, 2, 9, 5, 3, 76, 121, -121
        });
        stream = AsIntStream.of();
        assertArrayEquals(stream.toArray(), new int[] {});
    }

    @Test
    public void testAverage() {
        assertEquals(stream.average(),8.454545454545455, DELTA);
        stream = AsIntStream.of(-1, -2, -3, -4);
        assertEquals(stream.average(), -2.5, DELTA);
    }

    @Test
    public void testCount() {
        assertEquals(stream.count(), 11);
        stream = AsIntStream.of(-2, -1, 0, 1, 2, 9, 5, 3, 76, 121, -121);
        assertEquals(stream.filter(value -> (value == -121)).count(), 1);
    }

    @Test
    public void testMax() {
        assertEquals(stream.max(), 121);
        stream = AsIntStream.of(-1, -1, -1);
        assertEquals(stream.max(), -1);
        stream = AsIntStream.of(0);
        assertEquals(stream.max(), 0);
    }

    @Test
    public void testMin() {
        assertEquals(stream.min(), -121);
        stream = AsIntStream.of(1, 1, 1);
        assertEquals(stream.min(), 1);
        stream = AsIntStream.of(0);
        assertEquals(stream.min(), 0);
    }

    @Test
    public void testSum() {
        assertEquals(stream.sum(), 93);
        stream = AsIntStream.of();
        assertEquals(stream.sum(), 0);
    }

    @Test
    public void testFilter() {
        assertArrayEquals(stream.filter(value -> (value > 0)).toArray(),
                new int[] {1, 2, 9, 5, 3, 76, 121});
        stream = AsIntStream.of(-2, -1, 0, 1, 2, 9, 5, 3, 76, 121, -121);
        assertArrayEquals(stream.filter(value -> (true)).toArray(),
                new int[] {-2, -1, 0, 1, 2, 9, 5, 3, 76, 121, -121});
        stream = AsIntStream.of(-2, -1, 0, 1, 2, 9, 5, 3, 76, 121, -121);
        assertArrayEquals(stream.filter(value -> (false)).toArray(),
                new int[] {});
    }

    @Test
    public void testMap() {
        assertArrayEquals(stream.map(Math::abs).toArray(),
                new int[] {2, 1, 0, 1, 2, 9, 5, 3, 76, 121, 121});
        stream = AsIntStream.of(1, 2, 3, 4);
        assertArrayEquals(stream.map(operand -> (5-operand)).toArray(),
                new int[] {4, 3, 2, 1});
    }

    @Test
    public void testFlatMap() {
        stream = AsIntStream.of(1, -1, 2, 4);
        assertArrayEquals(stream.flatMap(value -> AsIntStream.of(value-1, value, value+1)).toArray(),
                new int[] {0, 1, 2, -2, -1, 0, 1, 2, 3, 3, 4, 5});

        stream = AsIntStream.of(1, 2, 3);
        assertArrayEquals(stream.flatMap(value -> AsIntStream.of(1)).toArray(),
                new int[] {1, 1, 1});
    }

    @Test
    public void testForEach() {
        int[] arr = new int[11];
        stream.forEach(new IntConsumer() {
            private int count = 0;
            @Override
            public void accept(int value) {
                arr[count++] = value;
            }
        });
        assertArrayEquals(arr, new int[] {-2, -1, 0, 1, 2, 9, 5, 3, 76, 121, -121});
    }

    @Test
    public void testReduce() {
        assertEquals(stream.reduce(0, (left, right) -> (left+1)), 11);
        stream = AsIntStream.of(1, 2, 3 -1, -2, 9);
        assertEquals(stream.reduce(0, (left, right) -> (left+2*right)), 24);
    }

}
