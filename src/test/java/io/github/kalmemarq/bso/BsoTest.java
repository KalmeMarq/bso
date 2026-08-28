package io.github.kalmemarq.bso;

import io.github.kalmemarq.bso.custom.UUIDType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

public class BsoTest {
    @TempDir
    Path tempDir;

    @Test
    void binaryRoundtripPrimitives() throws IOException {
        BsoMap map = new BsoMap();
        map.putByte("b", -12);
        map.putUByte("ub", 200);
        map.putBool("t", true);
        map.putBool("f", false);
        map.putShort("s", 400);
        map.put("us", new BsoUShort((short) 40000));
        map.putInt("i", 1_000_000);
        map.putUInt("ui", 3_000_000_000L);
        map.putLong("l", Long.MIN_VALUE);
        map.put("ul", new BsoULong(-1L));
        map.putFloat("fl", 1.5f);
        map.putDouble("d", Math.PI);
        map.putString("str", "hello");
        map.putString("utf", "café");

        assertBinaryRoundtrip(map);
    }

    @Test
    void binaryRoundtripPackedIntegers() throws IOException {
        BsoMap map = new BsoMap();
        map.putInt("byteSized", 100);
        map.putInt("shortSized", 1_000);
        map.putInt("intSized", 100_000);
        map.put("uByteSized", new BsoUInt(200));
        map.put("uShortSized", new BsoUInt(40_000));
        map.put("uIntSized", new BsoUInt(0x8000_0000));
        map.putLong("longByte", 50);
        map.putLong("longShort", 1_000);
        map.putLong("longInt", 100_000L);
        map.putLong("longFull", 1L << 40);

        assertBinaryRoundtrip(map);
    }

    @Test
    void binaryRoundtripContainersAndArrays() throws IOException {
        BsoList list = new BsoList();
        list.addString("a");
        list.addInt(2);
        list.add(new BsoMap());

        BsoMap map = new BsoMap();
        map.put("emptyMap", new BsoMap());
        map.put("emptyList", new BsoList());
        map.put("list", list);
        map.putByteArray("ba", new byte[]{1, -2, 3});
        map.putUByteArray("uba", new byte[]{(byte) 255, 1});
        map.putShortArray("sa", new short[]{-1, 300});
        map.put("usa", new BsoUShortArray(new short[]{(short) 40000}));
        map.putIntArray("ia", new int[]{1, Integer.MAX_VALUE, -3});
        map.putUIntArray("uia", new int[]{(int) 3_000_000_000L});
        map.putLongArray("la", new long[]{Long.MIN_VALUE, 0});
        map.putULongArray("ula", new long[]{-1L});
        map.putFloatArray("fa", new float[]{0.5f, -2f});
        map.putDoubleArray("da", new double[]{Double.MIN_VALUE, 1.0});

        assertBinaryRoundtrip(map);
    }

    @Test
    void binaryRoundtripLittleEndian() throws IOException {
        BsoMap map = new BsoMap();
        map.putInt("i", 0x01020304);
        map.putLong("l", 0x0102030405060708L);
        map.putFloat("f", 123.456f);
        map.putDouble("d", 123.456789);
        map.putString("s", "le");

        Path path = this.tempDir.resolve("le.bso");
        BsoUtils.write(path, map, BsoUtils.Endianess.LITTLE);
        BsoNodes.assertEquals(map, BsoUtils.read(path));
    }

    @Test
    void binaryRoundtripCompressed() throws IOException {
        BsoMap map = new BsoMap();
        map.putString("payload", "x".repeat(256));
        map.putInt("n", 42);

        Path path = this.tempDir.resolve("gz.bso");
        BsoUtils.writeCompressed(path, map);
        BsoNodes.assertEquals(map, BsoUtils.read(path));
    }

    @Test
    void binaryRoundtripCustomUuid() throws IOException {
        BsoUtils.registerCustomType(UUIDType.INSTANCE);
        try {
            UUID uuid = UUID.fromString("3df8b598-0c8c-4e0f-af96-b107f3b9934a");
            BsoCustom<UUID> node = new BsoCustom<>(UUIDType.INSTANCE, uuid);

            Path path = this.tempDir.resolve("uuid.bso");
            BsoUtils.write(path, node);
            BsoNode read = BsoUtils.read(path);
            Assertions.assertEquals(node, read);
        } finally {
            BsoUtils.unregisterAllCustomTypes();
        }
    }

    @Test
    void binaryWriteRejectsMissing() {
        Path path = this.tempDir.resolve("missing.bso");
        Assertions.assertThrows(IllegalArgumentException.class, () -> BsoUtils.write(path, BsoMissing.INSTANCE));
    }

    private void assertBinaryRoundtrip(BsoNode node) throws IOException {
        Path path = this.tempDir.resolve("roundtrip.bso");
        BsoUtils.write(path, node);
        BsoNodes.assertEquals(node, BsoUtils.read(path));
    }
}
