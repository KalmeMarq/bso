package io.github.kalmemarq.bso;

import io.github.kalmemarq.bso.SBsoReader.SBsoParseException;
import io.github.kalmemarq.bso.custom.BooleanArrayType;
import io.github.kalmemarq.bso.custom.UUIDType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

public class SBsoTest {
    @Test
    void mapRoundtripPreservesValues() throws IOException {
        BsoMap map = new BsoMap();
        map.putUByte("age", 21);
        map.putString("name", "Kalme");
        map.putFloat(".hey.", 0.55f);

        BsoList hobbies = new BsoList();
        hobbies.addString("coding");
        hobbies.addString("sleeping a ton");
        map.put("hobbies", hobbies);

        BsoNodes.assertEquals(map, SBsoUtils.read(SBsoUtils.stringify(map)));
    }

    @Test
    void minifiedStringifyUsesInsertionOrder() {
        BsoMap map = new BsoMap();
        map.putUByte("age", 21);
        map.putString("name", "Kalme");
        map.putFloat(".hey.", 0.55f);

        BsoList hobbies = new BsoList();
        hobbies.addString("coding");
        hobbies.addString("sleeping a ton");
        map.put("hobbies", hobbies);

        Assertions.assertEquals(
                "{age:21ub,name:\"Kalme\",.hey.:0.55f,hobbies:[\"coding\",\"sleeping a ton\"]}",
                SBsoUtils.stringify(map)
        );
    }

    @Test
    void customUuidStringifyAndParse() throws IOException {
        BsoUtils.registerCustomType(UUIDType.INSTANCE);
        try {
            UUID uuid = UUID.fromString("3df8b598-0c8c-4e0f-af96-b107f3b9934a");
            BsoCustom<UUID> node = new BsoCustom<>(UUIDType.INSTANCE, uuid);

            String text = SBsoUtils.stringify(node);
            Assertions.assertEquals("(uuid;3df8b598-0c8c-4e0f-af96-b107f3b9934a)", text);
            Assertions.assertEquals(node, SBsoUtils.read(text));
        } finally {
            BsoUtils.unregisterCustomType(UUIDType.INSTANCE);
        }
    }

    @Test
    void customBooleanArrayStringifyAndParse() throws IOException {
        BsoUtils.registerCustomType(BooleanArrayType.INSTANCE);
        try {
            BsoCustom<boolean[]> node = new BsoCustom<>(BooleanArrayType.INSTANCE, new boolean[]{true, false, true});
            BsoNode parsed = SBsoUtils.read(SBsoUtils.stringify(node));
            Assertions.assertTrue(parsed instanceof BsoCustom);
            Assertions.assertArrayEquals(node.value(), (boolean[]) ((BsoCustom<?>) parsed).value());
        } finally {
            BsoUtils.unregisterCustomType(BooleanArrayType.INSTANCE);
        }
    }

    @Test
    void smartMultilineTextRoundtrip() throws IOException {
        BsoMap map = new BsoMap();
        map.putString("text", "Hey brother\nHow are you?");

        BsoNode parsed = SBsoUtils.read(SBsoUtils.stringify(map, SBsoWriteOptions.PRETTY_SMART));
        Assertions.assertEquals(map.get("text").asString(), parsed.get("text").asString());
    }

    @Test
    void numericSuffixesAndRadices() throws IOException {
        Assertions.assertEquals(new BsoByte((byte) 21), SBsoUtils.read("21sb"));
        Assertions.assertEquals(new BsoByte((byte) 21), SBsoUtils.read("21b"));
        Assertions.assertEquals(new BsoUByte((byte) 200), SBsoUtils.read("200ub"));
        Assertions.assertEquals(new BsoShort((short) 400), SBsoUtils.read("400ss"));
        Assertions.assertEquals(new BsoUShort((short) 40000), SBsoUtils.read("40000us"));
        Assertions.assertEquals(new BsoInt(7), SBsoUtils.read("7"));
        Assertions.assertEquals(new BsoUInt((int) 3000000000L), SBsoUtils.read("3000000000u"));
        Assertions.assertEquals(new BsoLong(9L), SBsoUtils.read("9sl"));
        Assertions.assertEquals(new BsoULong(-1L), SBsoUtils.read("18446744073709551615ul"));
        Assertions.assertEquals(new BsoFloat(0.5f), SBsoUtils.read("0.5f"));
        Assertions.assertEquals(new BsoDouble(0.5), SBsoUtils.read("0.5d"));
        Assertions.assertEquals(new BsoInt(255), SBsoUtils.read("0xff"));
        Assertions.assertEquals(new BsoInt(5), SBsoUtils.read("0b101"));
        Assertions.assertEquals(BsoBool.TRUE, SBsoUtils.read("true"));
        Assertions.assertEquals(BsoBool.FALSE, SBsoUtils.read("false"));
    }

    @Test
    void stringEscapesAndQuotedKeys() throws IOException {
        BsoMap map = new BsoMap();
        map.putString("plain", "a\"b\nc");
        map.putInt("spaced key", 1);

        String text = SBsoUtils.stringify(map);
        Assertions.assertEquals("{plain:\"a\\\"b\\nc\",\"spaced key\":1}", text);
        BsoNodes.assertEquals(map, SBsoUtils.read(text));
        BsoNodes.assertEquals(map, SBsoUtils.read("{plain:\"a\\u0022b\\nc\",\"spaced key\": 1}"));
    }

    @Test
    void emptyContainers() throws IOException {
        Assertions.assertEquals(new BsoMap(), SBsoUtils.read("{}"));
        Assertions.assertEquals(new BsoList(), SBsoUtils.read("[]"));
        BsoNodes.assertEquals(new BsoByteArray(new byte[0]), SBsoUtils.read("[B;]"));
        BsoNodes.assertEquals(new BsoIntArray(new int[0]), SBsoUtils.read("[I;]"));
    }

    @Test
    void typedArraysRoundtrip() throws IOException {
        BsoMap map = new BsoMap();
        map.putByteArray("ba", new byte[]{1, -2});
        map.putUByteArray("uba", new byte[]{(byte) 255});
        map.putShortArray("sa", new short[]{3});
        map.put("usa", new BsoUShortArray(new short[]{(short) 40000}));
        map.putIntArray("ia", new int[]{4, -5});
        map.putUIntArray("uia", new int[]{(int) 3_000_000_000L});
        map.putLongArray("la", new long[]{6});
        map.putULongArray("ula", new long[]{-1L});
        map.putFloatArray("fa", new float[]{0.25f});
        map.putDoubleArray("da", new double[]{0.125});

        BsoNodes.assertEquals(map, SBsoUtils.read(SBsoUtils.stringify(map)));
    }

    @Test
    void prettySmartKeepsPrimitiveListsOnOneLine() {
        BsoList list = new BsoList();
        list.addInt(1);
        list.addInt(2);
        list.addInt(3);

        BsoMap map = new BsoMap();
        map.put("nums", list);

        Assertions.assertEquals("{\n  nums: [1, 2, 3]\n}", SBsoUtils.stringify(map, SBsoWriteOptions.PRETTY_SMART));
    }

    @Test
    void parseErrorsReportLocation() {
        SBsoParseException ex = Assertions.assertThrows(SBsoParseException.class, () -> SBsoUtils.read("{a:"));
        Assertions.assertTrue(ex.getMessage().contains("at "));
        Assertions.assertThrows(SBsoParseException.class, () -> SBsoUtils.read("{a 1}"));
        Assertions.assertThrows(SBsoParseException.class, () -> SBsoUtils.read("\"\\uXX\""));
        Assertions.assertThrows(SBsoParseException.class, () -> SBsoUtils.read("(unknown;1)"));
        Assertions.assertThrows(SBsoParseException.class, () -> SBsoUtils.read("{a:@}"));
        Assertions.assertThrows(SBsoParseException.class, () -> SBsoUtils.read("1 2"));
    }

    @Test
    void parsePreservesKeyOrder() throws IOException {
        BsoNode parsed = SBsoUtils.read("{first:1,second:2,third:3}");
        Assertions.assertEquals("{first:1,second:2,third:3}", SBsoUtils.stringify(parsed));
    }

    @Test
    void quotedKeysUnescape() throws IOException {
        BsoMap map = new BsoMap();
        map.put("say \"hi\"", new BsoInt(1));

        String text = SBsoUtils.stringify(map);
        BsoNodes.assertEquals(map, SBsoUtils.read(text));
    }
}
