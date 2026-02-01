package io.github.kalmemarq.bso;

import io.github.kalmemarq.bso.BsoCustom.BsoCustomType;

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

    private static final Map<Integer, BsoCustomType<?>> customTypes = new HashMap<>();
    protected static final Map<Class<?>, BsoCustomType<?>> customTypeByClazz = new HashMap<>();
    protected static final Map<String, BsoCustomType<?>> customTypeByName = new HashMap<>();

    public static <T> BsoCustomType<T> registerCustomType(BsoCustomType<T> type) {
        customTypes.put(type.getId(), type);
        customTypeByClazz.put(type.getClazz(), type);
        customTypeByName.put(type.getName(), type);
        return type;
    }

    public static <T> BsoCustomType<T> unregisterCustomType(BsoCustomType<T> type) {
        customTypes.remove(type.getId());
        customTypeByClazz.remove(type.getClazz());
        customTypeByName.remove(type.getName());
        return type;
    }

    public static void unregisterAllCustomTypes() {
        customTypes.clear();
    }

    /*
       bin -> str
            java -jar bso.jar b2s X Y --indent 3
       str -> bin
            java -jar bso.jar s2b X Y --le
     */
    static void main(String[] args) throws IOException {
        if (args.length >= 3) {
            if ("b2s".equals(args[0])) {
                Path inPath = Path.of(args[1]);
                Path outPath = Path.of(args[2]);

                int indent = 0;
                if (args.length >= 5 && "--indent".equals(args[3])) {
                    indent = Integer.parseInt(args[4]);
                }

                SBsoUtils.write(outPath, BsoUtils.read(inPath), new SBsoWriteOptions(indent, false, false));
            } else if ("s2b".equals(args[0])) {
                Path inPath = Path.of(args[1]);
                Path outPath = Path.of(args[2]);

                Endianess endianess = Endianess.BIG;
                if (args.length >= 4 && "--le".equals(args[3])) {
                    endianess = Endianess.LITTLE;
                }

                BsoUtils.write(outPath, SBsoUtils.read(inPath), endianess);
            } else {
                throw new IllegalArgumentException("Unknown command " + args[0]);
            }
        }
    }


    public static void write(Path path, BsoNode node) throws IOException {
        write(path, node, Endianess.BIG);
    }

    public static void writeCompressed(Path path, BsoNode node) throws IOException {
        writeCompressed(path, node, Endianess.BIG);
    }

    public static void write(Path path, BsoNode node, Endianess endianess) throws IOException {
        try (DataOutputStream output = new DataOutputStream(Files.newOutputStream(path))) {
            output.write(BSO_VERSION << 4 | (endianess == Endianess.BIG ? 0 : 0b0100));

            DataOutput out = endianess == Endianess.BIG ? output : new LittleEndianDataOutput(output);

            int ad = getBsoNodeAd(node);
            int id = getBsoNodeId(node);
            writeADID(out, ad, id);
            writeBsoNode(out, node, ad);
        }
    }

    public static void writeCompressed(Path path, BsoNode node, Endianess endianess) throws IOException {
        try (OutputStream outS = Files.newOutputStream(path)) {
            outS.write((BSO_VERSION << 4) | 0b1000);

            try (DataOutputStream output = new DataOutputStream(new GZIPOutputStream(outS))) {
                DataOutput out = endianess == Endianess.BIG ? output : new LittleEndianDataOutput(output);

                int ad = getBsoNodeAd(node);
                int id = getBsoNodeId(node);
                writeADID(out, ad, id);
                writeBsoNode(out, node, ad);
            }
        }
    }

    private static void writeADID(DataOutput out, int ad, int id) throws IOException {
        if (id < 16) { // 0AAA TTTT
            out.write((ad & 0b0111) << 4 | (id & 0b1111));
        } else if (id < 256) { // 10XX XXXX TTTT TTTT
            out.write(0b1000_0000 | (ad & 0b0011_1111));
            out.write(id & 0b1111_1111);
        } else if (id < 4096) { // 110X XXXX XXXX TTTT TTTT TTTT
            out.write(0b1100_0000 | ((ad >> 4) & 0b1_1111));
            out.write((ad & 0b1111) | (id >> 8) & 0b1111);
            out.write(id & 0b1111_1111);
        } else if (id < 262144) { // 1110 XXXX XXXX XXTT TTTT TTTT TTTT TTTT
            out.write(0b1110_0000 | (ad >> 6) & 0b1111);
            out.write(((ad & 0b11_1111) << 2) | (id >> 10) & 0b1111_1111);
            out.write((id >> 8) & 0b1111_1111);
            out.write(id & 0b1111_1111);
        }
    }

    public static BsoNode read(Path path) throws IOException {
        try (InputStream inS = Files.newInputStream(path)) {
            int header = inS.read();
            int version = (header >> 4) & 0xF;
            int config = header & 0xF;

            if (version != 0) {
                throw new IOException("Unknown BSO version " + version);
            }

            if ((config & 0b1000) == 0) {
                try (DataInputStream input = new DataInputStream(inS)) {
                    DataInput in = (config & 0b0100) == 0 ? input : new LittleEndianDataInput(input);

                    long adid = readADID(in);
                    int ad = (int) ((adid >> 32L) & 0xFFFFFFFFL);
                    int id = (int) (adid & 0xFFFFFFFFL);
                    return readBsoNode(in, id, ad);
                }
            } else {
                try (DataInputStream input = new DataInputStream(new GZIPInputStream(inS) )) {
                    DataInput in = (config & 0b0100) == 0 ? input : new LittleEndianDataInput(input);

                    long adid = readADID(in);
                    int ad = (int) ((adid >> 32L) & 0xFFFFFFFFL);
                    int id = (int) (adid & 0xFFFFFFFFL);
                    return readBsoNode(in, id, ad);
                }
            }
        }
    }

    private static long readADID(DataInput in) throws IOException {
         int b = in.readUnsignedByte();
         if ((b & 0b1000_0000) == 0) { // 0AAA TTTT
             long ad = b >> 4;
             long id = b & 0b1111;
             return ad << 32L | id;
         } else if ((b & 0b1100_0000) == 0b1000_0000) { // 10XX XXXX TTTT TTTT
             int c = in.readUnsignedByte();

             long ad = b & 0b0011_1111;
             long id = c;
             return ad << 32L | id;
         } else if ((b & 0b1110_0000) == 0b1100_0000) { // 110X XXXX XXXX TTTT TTTT TTTT
             int c = in.readUnsignedByte();
             int d = in.readUnsignedByte();

             long ad = (b & 0b1_1111) << 4 | d >> 4;
             long id = (c & 0xF) << 8 | d;
             return ad << 32L | id;
         } else if ((b & 0b1111_0000) == 0b1110_0000) { // 1110 XXXX XXXX XXTT TTTT TTTT TTTT TTTT (0xFF 0x3) (0x3 0xFF 0xFF)
             int c = in.readUnsignedByte();
             int d = in.readUnsignedByte();
             int e = in.readUnsignedByte();

             long ad = (b & 0b1111) << 6 | (c & 0b1111_1100) >> 2;
             long id = (c & 0b11) << 16 | d << 8 | e;
             return ad << 32L | id;
         }

         throw new IOException("Unknown adid");
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

                int length = ad == 0b0100 ? in.readInt() : ad == 0b0010 ? in.readUnsignedShort() : in.readUnsignedByte();
                map = new HashMap<>(length);

                for (int i = 0; i < length; ++i) {
                    long adid = readADID(in);
                    int ead = (int) ((adid >> 32L) & 0xFFFFFFFFL);
                    int eid = (int) (adid & 0xFFFFFFFFL);

                    String key = readBsoMapKey(in, ad);
                    BsoNode value = readBsoNode(in, eid, ead);
                    map.put(key, value);
                }

                return new BsoMap(map);
            }
            case 0b1000 -> {
                List<BsoNode> list;

                int length = ad == 0b0100 ? in.readInt() : ad == 0b0010 ? in.readUnsignedShort() : in.readUnsignedByte();
                list = new ArrayList<>(length);

                for (int i = 0; i < length; ++i) {
                    long adid = readADID(in);
                    int ead = (int) ((adid >> 32L) & 0xFFFFFFFFL);
                    int eid = (int) (adid & 0xFFFFFFFFL);

                    BsoNode value = readBsoNode(in, eid, ead);
                    list.add(value);
                }

                return new BsoList(list);
            }
            case 0b1001 -> {
                int length = ad == 0b0100 ? in.readInt() : ad == 0b0010 ? in.readUnsignedShort() : in.readUnsignedByte();
                byte[] array = new byte[length];
                in.readFully(array, 0, array.length);

                return (ad & 0b0001) == 0 ? new BsoByteArray(array) : new BsoUByteArray(array);
            }
            case 0b1010 -> {
                int length = ad == 0b0100 ? in.readInt() : ad == 0b0010 ? in.readUnsignedShort() : in.readUnsignedByte();
                short[] array = new short[length];
                for (int i = 0; i < length; ++i) {
                    array[i] = in.readShort();
                }

                return (ad & 0b0001) == 0 ? new BsoShortArray(array) : new BsoUShortArray(array);
            }
            default -> {
                BsoCustomType<?> customType = customTypes.get(id);
                if (customType != null) {
                    return customType.read(in, ad);
                }

                throw new IOException("Unknown id " + id);
            }
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

    @SuppressWarnings("unchecked")
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
                    int ead = getBsoNodeAd(entry.getValue());
                    int eid = getBsoNodeId(entry.getValue());
                    writeADID(out, ead, eid);

                    byte[] bytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
                    if ((ad & 0b1000) != 0) {
                        writeVarInt(out, bytes.length);
                    }

                    out.write(bytes);

                    if ((ad & 0b1000) == 0) {
                        out.writeByte(0);
                    }

                    writeBsoNode(out, entry.getValue(), ead);
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
                    int ead = getBsoNodeAd(entry);
                    int eid = getBsoNodeId(entry);
                    writeADID(out, ead, eid);
                    writeBsoNode(out, entry, ead);
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
            case BsoCustom<?> n -> ((BsoCustom<Object>) n).type().write(out, (BsoCustom<Object>) n);
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
        if (node instanceof BsoBool || node instanceof BsoByte || node instanceof BsoUByte) {
            return 0b0001;
        } else if (node instanceof BsoShort || node instanceof BsoUShort) {
            return 0b0010;
        } else if (node instanceof BsoInt || node instanceof BsoUInt) {
            return 0b0011;
        } else if (node instanceof BsoLong || node instanceof BsoULong) {
            return 0b0100;
        } else if (node instanceof BsoFloat || node instanceof BsoDouble) {
            return 0b0101;
        } else if (node instanceof BsoString) {
            return 0b0110;
        } else if (node instanceof BsoMap) {
            return 0b0111;
        } else if (node instanceof BsoList) {
            return 0b1000;
        } else if (node instanceof BsoByteArray || node instanceof BsoUByteArray) {
            return 0b1001;
        } else if (node instanceof BsoShortArray || node instanceof BsoUShortArray) {
            return 0b1010;
        } else if (node instanceof BsoIntArray || node instanceof BsoUIntArray) {
            return 0b1011;
        } else if (node instanceof BsoLongArray || node instanceof BsoULongArray) {
            return 0b1100;
        } else if (node instanceof BsoFloatArray || node instanceof BsoDoubleArray) {
            return 0b1101;
        } else if (node instanceof BsoCustom<?> n) {
            return n.type().getId();
        } else {
            throw new IllegalArgumentException("Unknown type");
        }
    }

    @SuppressWarnings("unchecked")
    private static int getBsoNodeAd(BsoNode node) {
        if (node instanceof BsoByte) {
            return 0b0000;
        } else if (node instanceof BsoUByte) {
            return 0b0001;
        } else if (node instanceof BsoBool(boolean value)) {
            return value ? 0b0110 : 0b0010;
        } else if (node instanceof BsoShort(short value)) {
            return value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE ? 0b0010 : 0b0000;
        } else if (node instanceof BsoUShort(short value)) {
            return (value & 0xFFFF) <= UBYTE_MAX ? 0b0011 : 0b0001;
        } else if (node instanceof BsoInt(int value)) {
            if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE)
                return 0b0010;
            else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE)
                return 0b0100;
            else
                return 0b0000;
        } else if (node instanceof BsoUInt(int value)) {
            long uvalue = (long)value & 0xFFFFFFFFL;
            if (uvalue <= UBYTE_MAX)
                return 0b0011;
            else if (uvalue <= USHORT_MAX)
                return 0b0101;
            else
                return 0b0001;
        } else if (node instanceof BsoLong(long value)) {
            if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE)
                return 0b0010;
            else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE)
                return 0b0100;
            else if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE)
                return 0b0110;
            else
                return 0b0000;
        } else if (node instanceof BsoULong(long value)) {
            if (Long.compareUnsigned(value, UBYTE_MAX) <= 0)
                return 0b0011;
            else if (Long.compareUnsigned(value, USHORT_MAX) <= 0)
                return 0b0101;
            else if (Long.compareUnsigned(value, UINT_MAX) <= 0)
                return 0b0111;
            else
                return 0b0001;
        } else if (node instanceof BsoFloat) {
            return 0b0000;
        } else if (node instanceof BsoDouble) {
            return 0b0001;
        } else if (node instanceof BsoString(String value)) {
            int len = value.length();
            return (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
        } else if (node instanceof BsoMap n) {
            int len = n.size();
            return (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
        } else if (node instanceof BsoList n) {
            int len = n.size();
            return (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
        } else if (node instanceof BsoByteArray n) {
            int len = n.size();
            return (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
        } else if (node instanceof BsoUByteArray n) {
            int len = n.size();
            return 0b0001 | ((len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100);
        } else if (node instanceof BsoShortArray n) {
            int len = n.size();
            return (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
        } else if (node instanceof BsoUShortArray n) {
            int len = n.size();
            return 0b0001 | ((len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100);
        } else if (node instanceof BsoIntArray n) {
            int len = n.size();
            return (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
        } else if (node instanceof BsoUIntArray n) {
            int len = n.size();
            return 0b0001 | ((len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100);
        } else if (node instanceof BsoLongArray n) {
            int len = n.size();
            return (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
        } else if (node instanceof BsoULongArray n) {
            int len = n.size();
            return 0b0001 | ((len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100);
        } else if (node instanceof BsoFloatArray n) {
            int len = n.size();
            return (len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100;
        } else if (node instanceof BsoDoubleArray n) {
            int len = n.size();
            return 0b0001 | ((len & 0xFFFFFF00) == 0 ? 0b0000 : (len & 0xFFFF0000) == 0 ? 0b0010 : 0b0100);
        } else if (node instanceof BsoCustom<?> n) {
            return ((BsoCustomType<Object>) n.type()).getAd((BsoCustom<Object>) n);
        } else {
            throw new IllegalArgumentException("Unknown type");
        }
    }

    public enum Endianess {
        BIG,
        LITTLE;

        Endianess getNative() {
            return ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN ? BIG : LITTLE;
        }
    }
}
