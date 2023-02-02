package me.kalmemarq.bso;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import me.kalmemarq.bso.reader.StringBSOReader;
import me.kalmemarq.bso.writer.BSOJsonWriter;
import me.kalmemarq.bso.writer.StringBSOWriter;

public final class BSOUtil {
    private BSOUtil() {}
    
    /**
     * Serializes BSO to SBSO (stringified BSO).
     */
    public static String toSBSO(BSOElement element) {
        return new StringBSOWriter().apply(element);
    }

    /**
     * Serializes BSO to stringified JSON.
     */
    public static String toSJson(BSOElement element) {
        return new BSOJsonWriter().apply(element);
    }

    /**
     * Deserializes BSO from SBSO (stringified BSO).
     */
    public static BSOElement fromSBSO(String str) {
        return new StringBSOReader().read(str);
    }

    /**
     * Deserializes a BSOMap from SBSO (stringified BSO). Be careful because it may throw an exception if the top level element isn't BSOMap.
     */
    public static BSOMap fromSBSOMap(String str) {
        return new StringBSOReader().readMap(str);
    }

    protected static final int BYTE_LENGTH = 0x20;
    protected static final int SHORT_LENGTH = 0x10;
    protected static final int INT_LENGTH = 0x00;
    protected static final int INDEFINITE_LENGTH = 0x30;
    protected static final int VARNUM_BYTE = 0x30;
    protected static final int VARNUM_SHORT = 0x20;
    protected static final int VARNUM_INT = 0x10;

    protected static void writeIndefiniteUTF8(DataOutput output, String value) throws IOException {
        if (value.contains("\0")) {
            throw new IllegalArgumentException("Null characters are not allowed in null-terminated strings.");
        }

        output.write(value.getBytes(StandardCharsets.UTF_8));
        output.writeByte((byte) 0);
    }

    protected static String readIndefiniteUTF8(DataInput input) throws IOException {
        byte[] arr = new byte[0xFFFF];

        byte b;
        int i = 0;
        while ((b = input.readByte()) != 0) {
            if (i >= arr.length) {
                byte[] temp = new byte[arr.length * 2];

                System.arraycopy(arr, 0, temp, 0, arr.length);

                arr = temp;
            }

            arr[i] = b;
            i++;
        }

        return new String(arr, 0, i, StandardCharsets.UTF_8);
    }

    protected static int readLength(DataInput input, int additionalData) throws IOException {
        return switch (additionalData) {
            case BYTE_LENGTH -> input.readUnsignedByte();
            case SHORT_LENGTH -> input.readUnsignedShort();
            case INT_LENGTH -> input.readByte();
            default -> {
                throw new RuntimeException("Unknown length type");
            }
        };
    }

    protected static void writeLength(DataOutput output, int length) throws IOException {
        if (length <= Byte.MAX_VALUE * 2 + 1) {
            output.writeByte(length);
        } else if (length <= Short.MAX_VALUE * 2 + 1) {
            output.writeShort(length);
        } else {
            output.writeInt(length);
        }
    }

    protected static void writeLength(BSOBufWriter writer, int length) throws IOException {
        if (length <= Byte.MAX_VALUE * 2 + 1) {
            writer.writeBEByte((byte) length);
        } else if (length <= Short.MAX_VALUE * 2 + 1) {
            writer.writeBEShort((short) length);
        } else {
            writer.writeBEInt(length);
        }
    }

    protected static int lengthAdditionalData(int length, boolean indefiniteLength) {
        if (indefiniteLength) return INDEFINITE_LENGTH;
        if (length <= Byte.MAX_VALUE * 2 + 1) {
            return BYTE_LENGTH;
        } else if (length <= Short.MAX_VALUE * 2 + 1) {
            return SHORT_LENGTH;
        }
        return INT_LENGTH;
    }
}
