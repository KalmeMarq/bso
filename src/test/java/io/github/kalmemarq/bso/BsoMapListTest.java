package io.github.kalmemarq.bso;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class BsoMapListTest {
    @Test
    void mapGetHasAndPath() {
        BsoMap map = new BsoMap();
        map.putInt("a", 7);

        Assertions.assertTrue(map.has("a"));
        Assertions.assertFalse(map.has("missing"));
        Assertions.assertEquals(7, map.get("a").asInt());
        Assertions.assertTrue(map.path("missing").isMissing());
        Assertions.assertEquals(0, map.path("missing").asInt());
        Assertions.assertEquals(-1, map.path("missing").asInt(-1));
    }

    @Test
    void mapCopyIsDeepAndIndependent() {
        BsoMap nested = new BsoMap();
        nested.putString("k", "v");

        BsoMap map = new BsoMap();
        map.put("nested", nested);

        BsoMap copy = (BsoMap) map.copy();
        nested.putString("k", "changed");
        copy.get("nested").put("extra", new BsoInt(1));

        Assertions.assertEquals("changed", map.get("nested").get("k").asString());
        Assertions.assertEquals("v", copy.get("nested").get("k").asString());
        Assertions.assertFalse(map.get("nested").has("extra"));
        Assertions.assertTrue(copy.get("nested").has("extra"));
    }

    @Test
    void mapPutRejectsMissing() {
        BsoMap map = new BsoMap();
        Assertions.assertThrows(IllegalArgumentException.class, () -> map.put("x", null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> map.put("x", BsoMissing.INSTANCE));
        Assertions.assertFalse(map.has("x"));
    }

    @Test
    void primitiveDefaultsOnWrongType() {
        BsoString node = new BsoString("nope");
        Assertions.assertEquals(0, node.asInt());
        Assertions.assertEquals(9, node.asInt(9));
        Assertions.assertEquals("", new BsoInt(3).asString());
        Assertions.assertEquals("fallback", new BsoInt(3).asString("fallback"));
        Assertions.assertFalse(BsoMissing.INSTANCE.asBool());
        Assertions.assertTrue(BsoMissing.INSTANCE.asBool(true));
    }

    @Test
    void listHasAndRemove() {
        BsoList list = new BsoList();
        list.addInt(1);
        list.addInt(2);
        list.addInt(3);

        Assertions.assertTrue(list.has(0));
        Assertions.assertTrue(list.has(2));
        Assertions.assertFalse(list.has(-1));
        Assertions.assertFalse(list.has(3));
        Assertions.assertEquals(2, list.remove(1).asInt());
        Assertions.assertEquals(2, list.size());
        Assertions.assertEquals(3, list.get(1).asInt());
        Assertions.assertNull(list.get(-1));
        Assertions.assertNull(list.get(3));
        Assertions.assertTrue(list.path(3).isMissing());
    }

    @Test
    void listAddRejectsMissing() {
        BsoList list = new BsoList();
        Assertions.assertThrows(IllegalArgumentException.class, () -> list.add(null));
        Assertions.assertThrows(IllegalArgumentException.class, () -> list.add(BsoMissing.INSTANCE));
        Assertions.assertEquals(0, list.size());
    }

    @Test
    void listCopyIsIndependent() {
        BsoList list = new BsoList();
        BsoMap item = new BsoMap();
        item.putInt("n", 1);
        list.add(item);

        BsoList copy = (BsoList) list.copy();
        item.putInt("n", 99);

        Assertions.assertEquals(99, list.get(0).get("n").asInt());
        Assertions.assertEquals(1, copy.get(0).get("n").asInt());
    }

    @Test
    void nodeTypeQueries() {
        Assertions.assertTrue(new BsoMap().isMap());
        Assertions.assertTrue(new BsoList().isList());
        Assertions.assertTrue(new BsoIntArray(new int[]{1}).isArray());
        Assertions.assertTrue(new BsoInt(3).isNumber());
        Assertions.assertTrue(new BsoULong(-1L).isNumber());
        Assertions.assertFalse(new BsoString("x").isNumber());
        Assertions.assertTrue(new BsoString("x").isString());
        Assertions.assertFalse(BsoBool.TRUE.isNumber());
        Assertions.assertTrue(BsoBool.TRUE.isBool());
        Assertions.assertFalse(new BsoMap().isList());
        Assertions.assertEquals(40000, new BsoUShort((short) 40000).asNumber().intValue());
        Assertions.assertEquals(200, new BsoUByte((byte) 200).asNumber().intValue());
    }
}
