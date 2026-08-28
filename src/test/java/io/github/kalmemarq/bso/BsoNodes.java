package io.github.kalmemarq.bso;

import org.junit.jupiter.api.Assertions;

import java.util.Map;

final class BsoNodes {
    static void assertEquals(BsoNode expected, BsoNode actual) {
        Assertions.assertNotNull(actual, "actual node was null");
        Assertions.assertEquals(expected.getClass(), actual.getClass(), "node type");

        switch (expected) {
            case BsoByteArray(byte[] values) -> Assertions.assertArrayEquals(values, ((BsoByteArray) actual).values());
            case BsoUByteArray(byte[] values) -> Assertions.assertArrayEquals(values, ((BsoUByteArray) actual).values());
            case BsoShortArray(short[] values) -> Assertions.assertArrayEquals(values, ((BsoShortArray) actual).values());
            case BsoUShortArray(short[] values) -> Assertions.assertArrayEquals(values, ((BsoUShortArray) actual).values());
            case BsoIntArray(int[] values) -> Assertions.assertArrayEquals(values, ((BsoIntArray) actual).values());
            case BsoUIntArray(int[] values) -> Assertions.assertArrayEquals(values, ((BsoUIntArray) actual).values());
            case BsoLongArray(long[] values) -> Assertions.assertArrayEquals(values, ((BsoLongArray) actual).values());
            case BsoULongArray(long[] values) -> Assertions.assertArrayEquals(values, ((BsoULongArray) actual).values());
            case BsoFloatArray(float[] values) -> Assertions.assertArrayEquals(values, ((BsoFloatArray) actual).values());
            case BsoDoubleArray(double[] values) -> Assertions.assertArrayEquals(values, ((BsoDoubleArray) actual).values());
            case BsoMap map -> {
                BsoMap other = (BsoMap) actual;
                Assertions.assertEquals(map.size(), other.size(), "map size");
                for (Map.Entry<String, BsoNode> entry : map.properties()) {
                    Assertions.assertTrue(other.has(entry.getKey()), "missing key " + entry.getKey());
                    assertEquals(entry.getValue(), other.get(entry.getKey()));
                }
            }
            case BsoList list -> {
                BsoList other = (BsoList) actual;
                Assertions.assertEquals(list.size(), other.size(), "list size");
                for (int i = 0; i < list.size(); i++) {
                    assertEquals(list.get(i), other.get(i));
                }
            }
            default -> Assertions.assertEquals(expected, actual);
        }
    }

    private BsoNodes() {
    }
}
