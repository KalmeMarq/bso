package me.kalmemarq.bso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BSOUtils {
    public static void write(File file, BSOElement element) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file); DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
            write(element, dataOutputStream);
        }
    }

    public static void writeCompressed(File file, BSOElement element) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file); DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)))) {
            write(element, dataOutputStream);
        }
    }

    private static void write(BSOElement element, DataOutput outputStream) throws IOException {
        outputStream.writeByte(element.getTypeId() + element.getAdditionalData());
        element.write(outputStream);
    }

    public static BSOMap read(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file); DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            return read(dataInputStream);
        }
    }

    public static BSOMap readCompressed(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file); DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(inputStream)))) {
            return read(dataInputStream);
        }
    }

    private static BSOMap read(DataInput inputStream) throws IOException {
        byte b = inputStream.readByte();
        return BSOTypes.MAP.read(inputStream, (byte) (b & 0xF0));
    }

    protected static final int BYTE_LENGTH = 0x20;
    protected static final int SHORT_LENGTH = 0x10;
    protected static final int INT_LENGTH = 0x00;
    protected static final int INDEFINITE_LENGTH = 0x30;
    protected static final int VARNUM_BYTE = 0x30;
    protected static final int VARNUM_SHORT = 0x20;
    protected static final int VARNUM_INT = 0x10;

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

    protected static int lengthAdditionalData(int length, boolean indefiniteLength) {
        if (indefiniteLength) return INDEFINITE_LENGTH;
        if (length <= Byte.MAX_VALUE * 2 + 1) {
            return BYTE_LENGTH;
        } else if (length <= Short.MAX_VALUE * 2 + 1) {
            return SHORT_LENGTH;
        }
        return 0;
    }
}