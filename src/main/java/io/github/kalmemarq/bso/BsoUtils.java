package io.github.kalmemarq.bso;

import java.io.*;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BsoUtils {
    private static final int BSO_VERSION = 0;
    private static final int UBYTE_MAX = Byte.MAX_VALUE * 2 + 1;
    private static final int USHORT_MAX = Short.MAX_VALUE * 2 + 1;
    private static final long UINT_MAX = Integer.MAX_VALUE * 2L + 1L;

    public static void write(Path path, BsoNode node) throws IOException {
        write(path, node, Endianess.BIG);
    }

    public static void writeCompressed(Path path, BsoNode node) throws IOException {
        writeCompressed(path, node, Endianess.BIG);
    }

    public static void write(Path path, BsoNode node, Endianess endianess) throws IOException {
        try (DataOutputStream output = new DataOutputStream(Files.newOutputStream(path))) {
            output.write(BSO_VERSION << 4);

            DataOutput out = endianess.get() == Endianess.BIG ? output : new LittleEndianDataOutput(output);

            int adid = getBsoNodeAd(node) << 4 | getBsoNodeId(node);
            out.writeByte(adid);
            writeBsoNode(out, node, (adid >> 4) & 0xF);
        }
    }

    public static void writeCompressed(Path path, BsoNode node, Endianess endianess) throws IOException {
        try (OutputStream outS = Files.newOutputStream(path)) {
            outS.write(BSO_VERSION << 4 | 0b1000);

            try (DataOutputStream output = new DataOutputStream(new GZIPOutputStream(outS))) {
                DataOutput out = endianess.get() == Endianess.BIG ? output : new LittleEndianDataOutput(output);

                int adid = getBsoNodeAd(node) << 4 | getBsoNodeId(node);
                out.writeByte(adid);
                writeBsoNode(out, node, (adid >> 4) & 0xF);
            }
        }
    }

    public static BsoNode read(Path path) throws IOException {
        return read(path, Endianess.BIG);
    }

    public static BsoNode read(Path path, Endianess endianess) throws IOException {
        try (InputStream inS = Files.newInputStream(path)) {
            int header = inS.read();
            int version = (header >> 4) & 0xF;
            int config = header & 0xF;

            if (version != 0) {
                throw new IOException("Unknown BSO version " + version);
            }

            if ((config & 0b1000) == 0) {
                try (DataInputStream input = new DataInputStream(inS)) {
                    DataInput in = endianess.get() == Endianess.BIG ? input : new LittleEndianDataInput(input);

                    int adid = in.readUnsignedByte();
                    int ad = (adid >> 4) & 0xF;
                    int id = adid & 0xF;
                    return readBsoNode(in, id, ad);
                }
            } else {
                try (DataInputStream input = new DataInputStream(new GZIPInputStream(inS) )) {
                    DataInput in = endianess.get() == Endianess.BIG ? input : new LittleEndianDataInput(input);

                    int adid = in.readUnsignedByte();
                    int ad = (adid >> 4) & 0xF;
                    int id = adid & 0xF;
                    return readBsoNode(in, id, ad);
                }
            }
        }
    }

    public static BsoNode readBsoNode(DataInput in, int id, int ad) throws IOException {
        switch (id) {
            case 0b0001 -> {
                if (ad == 0b0000)
                    return new BsoByte(in.readByte());
                if (ad == 0b0001)
                    return new BsoUByte((byte) in.readUnsignedByte());
                if (ad == 0b0010)
                    return new BsoBool(false);
                if (ad == 0b0110)
                    return new BsoBool(true);

                throw new IOException("Unknown additional data");
            }
            case 0b0010 -> {
                if ((ad & 0b0001) == 0) {
                    return new BsoShort((ad & 0b0010) == 0 ? in.readShort() : in.readByte());
                } else {
                    return new BsoUShort((ad & 0b0010) == 0 ? (short) in.readUnsignedShort() : (short) in.readUnsignedByte());
                }
            }
            case 0b0011 -> {
                if ((ad & 0b0001) == 0) {
                    if ((ad & 0b0010) != 0)
                        return new BsoInt(in.readByte());
                    else if ((ad & 0b0100) != 0)
                        return new BsoInt(in.readShort());
                    else
                        return new BsoInt(in.readInt());
                } else {
                    if ((ad & 0b0010) != 0)
                        return new BsoUInt(in.readUnsignedByte());
                    else if ((ad & 0b0100) != 0)
                        return new BsoUInt(in.readUnsignedShort());
                    else
                        return new BsoUInt(in.readInt());
                }
            }
            case 0b0100 -> {
                if ((ad & 0b0001) == 0) {
                    if ((ad & 0b0110) == 0b0010)
                        return new BsoLong(in.readByte());
                    else if ((ad & 0b0110) == 0b0100)
                        return new BsoLong(in.readShort());
                    else if ((ad & 0b0110) == 0b0110)
                        return new BsoLong(in.readInt());
                    else
                        return new BsoLong(in.readLong());
                } else {
                    if ((ad & 0b0110) == 0b0010)
                        return new BsoULong(in.readUnsignedByte());
                    else if ((ad & 0b0110) == 0b0100)
                        return new BsoULong(in.readUnsignedShort());
                    else if ((ad & 0b0110) == 0b0110)
                        return new BsoULong(in.readInt());
                    else
                        return new BsoULong(in.readLong());
                }
            }
            case 0b0101 -> {
                return ad == 0 ? new BsoFloat(in.readFloat()) : new BsoDouble(in.readDouble());
            }
            case 0b0110 -> {
                if (ad == 0b0001) {
                    byte[] buf = new byte[128];
                    int cur = 0;
                    byte val;
                    while ((val = in.readByte()) != 0) {
                        if (cur + 1 >= buf.length) {
                            buf = Arrays.copyOf(buf, buf.length * 2);
                        }

                        buf[cur++] = val;
                    }

                    return new BsoString(new String(buf, 0, cur, StandardCharsets.UTF_8));
                } else {
                    int length = ad == 0b0100 ? in.readInt() : ad == 0b0010 ? in.readUnsignedShort() : in.readUnsignedByte();
                    byte[] buf = new byte[length];
                    in.readFully(buf, 0, buf.length);
                    return new BsoString(new String(buf, StandardCharsets.UTF_8));
                }
            }
            case 0b0111 -> {
                Map<String, BsoNode> map;

                if ((ad & 0b0001) == 0) {
                    int length = ad == 0b0100 ? in.readInt() : ad == 0b0010 ? in.readUnsignedShort() : in.readUnsignedByte();
                    map = new HashMap<>(length);

                    for (int i = 0; i < length; ++i) {
                        int adid = in.readByte();
                        String key = readBsoMapKey(in, ad);
                        BsoNode value = readBsoNode(in, adid & 0xF, (adid >> 4) & 0xF);
                        map.put(key, value);
                    }
                } else {
                    map = new HashMap<>();
                    byte adid;
                    while ((adid = in.readByte()) != 0) {
                        String key = readBsoMapKey(in, ad);
                        BsoNode value = readBsoNode(in, adid & 0xF, (adid >> 4) & 0xF);
                        map.put(key, value);
                    }
                }

                return new BsoMap(map);
            }
            case 0b1000 -> {
                List<BsoNode> list;

                if ((ad & 0b0001) == 0) {
                    int length = ad == 0b0100 ? in.readInt() : ad == 0b0010 ? in.readUnsignedShort() : in.readUnsignedByte();
                    list = new ArrayList<>(length);

                    for (int i = 0; i < length; ++i) {
                        int adid = in.readByte();
                        BsoNode value = readBsoNode(in, adid & 0xF, (adid >> 4) & 0xF);
                        list.add(value);
                    }
                } else {
                    list = new ArrayList<>();
                    byte adid;
                    while ((adid = in.readByte()) != 0) {
                        BsoNode value = readBsoNode(in, adid & 0xF, (adid >> 4) & 0xF);
                        list.add(value);
                    }
                }

                return new BsoList(list);
            }
            default -> throw new IOException("Unknown id " + id);
        }
    }

    private static String readBsoMapKey(DataInput in, int ad) throws IOException {
        if ((ad & 0b1000) == 0b1000) {
            // prefix
            int length = readVarInt(in);
            byte[] buf = new byte[length];
            in.readFully(buf);
            return new String(buf, StandardCharsets.UTF_8);
        } else {
            byte[] buf = new byte[32];
            int cur = 0;
            byte val;
            while ((val = in.readByte()) != 0) {
                if (cur + 1 >= buf.length) {
                    buf = Arrays.copyOf(buf, buf.length * 2);
                }
                buf[cur++] = val;
            }
            return new String(buf, 0, cur, StandardCharsets.UTF_8);
        }
    }

    public static void writeBsoNode(DataOutput out, BsoNode node, int ad) throws IOException {
        switch (node) {
            case BsoMissing _ -> throw new IllegalArgumentException("Missing is not allowed");
            case BsoByte(byte value) -> out.write(value);
            case BsoUByte(byte value) -> out.write(value & 0xFF);
            case BsoShort(short value) -> {
                if ((ad & 0b0010) != 0)
                    out.writeByte(value);
                else
                    out.writeShort(value);
            }
            case BsoUShort(short value) -> {
                if ((ad & 0b0010) != 0)
                    out.writeByte(value & 0xFF);
                else
                    out.writeShort(value & 0xFFFF);
            }
            case BsoInt(int value) -> {
                if ((ad & 0b0010) != 0)
                    out.writeByte(value);
                else if ((ad & 0b0100) != 0)
                    out.writeShort(value);
                else
                    out.writeInt(value);
            }
            case BsoUInt(int value) -> {
                long uvalue = (long)value & 0xFFFFFFFFL;
                if ((ad & 0b0010) != 0)
                    out.writeByte((int) (uvalue & 0xFF));
                else if ((ad & 0b0110) != 0)
                    out.writeShort((int) (uvalue & 0xFFFF));
                else
                    out.writeInt((int) uvalue);
            }
            case BsoLong(long value) -> {
                if ((ad & 0b0110) == 0b0010)
                    out.writeByte((int) value);
                else if ((ad & 0b0110) == 0b0100)
                    out.writeShort((int) value);
                else if ((ad & 0b0110) == 0b0110)
                    out.writeInt((int) value);
                else
                    out.writeLong(value);
            }
            case BsoULong(long value) -> {
                if ((ad & 0b0110) == 0b0010)
                    out.write((int) (value & 0xFFL));
                else if ((ad & 0b0110) == 0b0100)
                    out.writeShort((int) (value & 0xFFFFL));
                else if ((ad & 0b0110) == 0b0110)
                    out.writeInt((int) (value & 0xFFFFFFFFL));
                else
                    out.writeLong(value);
            }
            case BsoFloat(float value) -> out.writeFloat(value);
            case BsoDouble(double value) -> out.writeDouble(value);
            case BsoBool _ -> {}
            case BsoString(String value) -> {
                byte[] bytes = value.getBytes(StandardCharsets.UTF_8);

                if (ad == 0b0100) {
                    out.writeInt(bytes.length);
                } else if (ad == 0b0010) {
                    out.writeShort(bytes.length & 0xFFFF);
                } else if (ad == 0b0000) {
                    out.writeByte(bytes.length & 0xFF);
                }

                out.write(bytes);

                if (ad == 0b0001) {
                    out.write(0);
                }
            }
            case BsoMap n -> {
                if ((ad & 0b0110) == 0b0000) {
                    out.writeByte(n.size() & 0xFF);
                } else if ((ad & 0b0110) == 0b0010) {
                    out.writeShort(n.size() & 0xFFFF);
                } else if ((ad & 0b0110) == 0b0100) {
                    out.writeInt(n.size());
                }

                for (var entry : node.properties()) {
                    int adid = getBsoNodeAd(entry.getValue()) << 4 | getBsoNodeId(entry.getValue());
                    out.writeByte(adid);

                    byte[] bytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
                    if ((ad & 0b1000) != 0) {
                        writeVarInt(out, bytes.length);
                    }

                    out.write(bytes);

                    if ((ad & 0b1000) == 0) {
                        out.writeByte(0);
                    }

                    writeBsoNode(out, entry.getValue(), (adid >> 4) & 0xF);
                }
            }
            case BsoList n -> {
                if ((ad & 0b1110) == 0b0000) {
                    out.writeByte(n.size() & 0xFF);
                } else if ((ad & 0b1110) == 0b0010) {
                    out.writeShort(n.size() & 0xFFFF);
                } else if ((ad & 0b1110) == 0b0100) {
                    out.writeInt(n.size());
                }

                for (BsoNode entry : node) {
                    int adid = getBsoNodeAd(entry) << 4 | getBsoNodeId(entry);
                    out.writeByte(adid);
                    writeBsoNode(out, entry, (adid >> 4) & 0xF);
                }
            }
            case BsoByteArray(byte[] values) -> {
                if ((ad & 0b1110) == 0b0000) {
                    out.writeByte(values.length & 0xFF);
                } else if ((ad & 0b1110) == 0b0010) {
                    out.writeShort(values.length & 0xFFFF);
                } else if ((ad & 0b1110) == 0b0100) {
                    out.writeInt(values.length);
                }

                out.write(values);
            }
            case BsoUByteArray(byte[] values) -> {
                if ((ad & 0b1110) == 0b0000) {
                    out.writeByte(values.length & 0xFF);
                } else if ((ad & 0b1110) == 0b0010) {
                    out.writeShort(values.length & 0xFFFF);
                } else if ((ad & 0b1110) == 0b0100) {
                    out.writeInt(values.length);
                }

                out.write(values);
            }
            case BsoShortArray(short[] values) -> {
                if ((ad & 0b1110) == 0b0000) {
                    out.writeByte(values.length & 0xFF);
                } else if ((ad & 0b1110) == 0b0010) {
                    out.writeShort(values.length & 0xFFFF);
                } else if ((ad & 0b1110) == 0b0100) {
                    out.writeInt(values.length);
                }

                for (short value : values) {
                    out.writeShort(value);
                }
            }
            case BsoUShortArray(short[] values) -> {
                if ((ad & 0b1110) == 0b0000) {
                    out.writeByte(values.length & 0xFF);
                } else if ((ad & 0b1110) == 0b0010) {
                    out.writeShort(values.length & 0xFFFF);
                } else if ((ad & 0b1110) == 0b0100) {
                    out.writeInt(values.length);
                }

                for (short value : values) {
                    out.writeShort(value);
                }
            }
            case BsoIntArray(int[] values) -> {
                if ((ad & 0b1110) == 0b0000) {
                    out.writeByte(values.length & 0xFF);
                } else if ((ad & 0b1110) == 0b0010) {
                    out.writeShort(values.length & 0xFFFF);
                } else if ((ad & 0b1110) == 0b0100) {
                    out.writeInt(values.length);
                }

                for (int value : values) {
                    out.writeInt(value);
                }
            }
            case BsoUIntArray(int[] values) -> {
                if ((ad & 0b1110) == 0b0000) {
                    out.writeByte(values.length & 0xFF);
                } else if ((ad & 0b1110) == 0b0010) {
                    out.writeShort(values.length & 0xFFFF);
                } else if ((ad & 0b1110) == 0b0100) {
                    out.writeInt(values.length);
                }

                for (int value : values) {
                    out.writeInt(value);
                }
            }
            case BsoLongArray(long[] values) -> {
                if ((ad & 0b1110) == 0b0000) {
                    out.writeByte(values.length & 0xFF);
                } else if ((ad & 0b1110) == 0b0010) {
                    out.writeShort(values.length & 0xFFFF);
                } else if ((ad & 0b1110) == 0b0100) {
                    out.writeInt(values.length);
                }

                for (long value : values) {
                    out.writeLong(value);
                }
            }
            case BsoULongArray(long[] values) -> {
                if ((ad & 0b1110) == 0b0000) {
                    out.writeByte(values.length & 0xFF);
                } else if ((ad & 0b1110) == 0b0010) {
                    out.writeShort(values.length & 0xFFFF);
                } else if ((ad & 0b1110) == 0b0100) {
                    out.writeInt(values.length);
                }

                for (long value : values) {
                    out.writeLong(value);
                }
            }
            case BsoFloatArray(float[] values) -> {
                if ((ad & 0b1110) == 0b0000) {
                    out.writeByte(values.length & 0xFF);
                } else if ((ad & 0b1110) == 0b0010) {
                    out.writeShort(values.length & 0xFFFF);
                } else if ((ad & 0b1110) == 0b0100) {
                    out.writeInt(values.length);
                }

                for (float value : values) {
                    out.writeFloat(value);
                }
            }
            case BsoDoubleArray(double[] values) -> {
                if ((ad & 0b1110) == 0b0000) {
                    out.writeByte(values.length & 0xFF);
                } else if ((ad & 0b1110) == 0b0010) {
                    out.writeShort(values.length & 0xFFFF);
                } else if ((ad & 0b1110) == 0b0100) {
                    out.writeInt(values.length);
                }

                for (double value : values) {
                    out.writeDouble(value);
                }
            }
        }
    }

    private static int readVarInt(DataInput in) throws IOException {
        int shift = 0;
        int result = 0;
        while (shift < 32) {
            final byte b = in.readByte();
            result |= (b & 0x7F) << shift;
            if ((b & 0x80) == 0) {
                return result;
            }
            shift += 7;
        }
        throw new RuntimeException("Malformed VarInt");
    }

    private static void writeVarInt(DataOutput out, int value) throws IOException {
        while (true) {
            if ((value & ~0x7F) == 0) {
                out.write(value);
                return;
            } else {
                out.write((value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }

    private static int getBsoNodeId(BsoNode node) {
        return switch (node) {
            case BsoMissing _ -> throw new IllegalArgumentException("Missing is not allowed");
            case BsoByte _, BsoUByte _, BsoBool _ -> 0b0001;
            case BsoShort _, BsoUShort _ -> 0b0010;
            case BsoInt _, BsoUInt _ -> 0b0011;
            case BsoLong _, BsoULong _ -> 0b0100;
            case BsoFloat _, BsoDouble _ -> 0b0101;
            case BsoString _ -> 0b0110;
            case BsoMap _ -> 0b0111;
            case BsoList _ -> 0b1000;
            case BsoByteArray _, BsoUByteArray _ -> 0b1001;
            case BsoShortArray _, BsoUShortArray _ -> 0b1010;
            case BsoIntArray _, BsoUIntArray _ -> 0b1011;
            case BsoLongArray _, BsoULongArray _ -> 0b1100;
            case BsoFloatArray _, BsoDoubleArray _ -> 0b1101;
        };
    }

    private static int getBsoNodeAd(BsoNode node) {
        return switch (node) {
            case BsoMissing _ -> throw new IllegalArgumentException("Missing is not allowed");
            case BsoByte _ -> 0b0000;
            case BsoUByte _ -> 0b0001;
            case BsoBool(boolean value) -> value ? 0b0110 : 0b0010;
            case BsoShort(short value) -> value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE ? 0b0010 : 0b0000;
            case BsoUShort(short value) -> (value & 0xFFFF) <= UBYTE_MAX ? 0b0011 : 0b0001;
            case BsoInt(int value) -> {
                if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE)
                    yield 0b0010;
                else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE)
                    yield 0b0100;
                else
                    yield 0b0000;
            }
            case BsoUInt(int value) -> {
                long uvalue = (long)value & 0xFFFFFFFFL;
                if (uvalue <= UBYTE_MAX)
                    yield 0b0011;
                else if (uvalue <= USHORT_MAX)
                    yield 0b0101;
                else
                    yield 0b0001;
            }
            case BsoLong(long value) -> {
                if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE)
                    yield 0b0010;
                else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE)
                    yield 0b0100;
                else if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE)
                    yield 0b0110;
                else
                    yield 0b0000;
            }
            case BsoULong(long value) -> {
                if (Long.compareUnsigned(value, UBYTE_MAX) <= 0)
                    yield 0b0011;
                else if (Long.compareUnsigned(value, USHORT_MAX) <= 0)
                    yield 0b0101;
                else if (Long.compareUnsigned(value, UINT_MAX) <= 0)
                    yield 0b0111;
                else
                    yield 0b0001;
            }
            case BsoFloat _ -> 0b0000;
            case BsoDouble _ -> 0b0001;
            case BsoString(String value) -> {
                int len = value.length();
                yield (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
            }
            case BsoMap n -> {
                int len = n.size();
                yield (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
            }
            case BsoList n -> {
                int len = n.size();
                yield (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
            }
            case BsoByteArray n -> {
                int len = n.size();
                yield (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
            }
            case BsoUByteArray n -> {
                int len = n.size();
                yield (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
            }
            case BsoShortArray n -> {
                int len = n.size();
                yield (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
            }
            case BsoUShortArray n -> {
                int len = n.size();
                yield (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
            }
            case BsoIntArray n -> {
                int len = n.size();
                yield (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
            }
            case BsoUIntArray n -> {
                int len = n.size();
                yield (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
            }
            case BsoLongArray n -> {
                int len = n.size();
                yield (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
            }
            case BsoULongArray n -> {
                int len = n.size();
                yield (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
            }
            case BsoFloatArray n -> {
                int len = n.size();
                yield (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
            }
            case BsoDoubleArray n -> {
                int len = n.size();
                yield 0b0001 | ((len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100);
            }
        };
    }

    public enum Endianess {
        NATIVE,
        BIG,
        LITTLE;

        Endianess get() {
            return switch (this) {
                case NATIVE -> ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ? BIG : LITTLE;
                default -> this;
            };
        }
    }
}
