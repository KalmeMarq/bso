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
import java.nio.ByteOrder;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import me.kalmemarq.bso.reader.BSOOutputStream;
import me.kalmemarq.bso.writer.BSOInputStream;

public final class BSOIo {
    private BSOIo() {}

    /**
     * Writes a BSO element to a {@link File}.
     * @param file File to which the BSO will be written into
     * @param element Element to be written
     */
    public static void write(File file, BSOElement element) throws IOException {
        write(file, element, false);
    }

    /**
     * Writes a BSO element to a {@link File} with GZip compression.
     * @param file File to which the BSO will be written into
     * @param element Element to be written
     */
    public static void writeCompressed(File file, BSOElement element) throws IOException {
        write(file, element, true);
    }

    /**
     * Writes a BSO element to a {@link File}.
     * @param file File to which the BSO will be written into
     * @param element Element to be written
     * @param compressed If it should use GZip compression
     */
    public static void write(File file, BSOElement element, boolean compressed) throws IOException {
        if (compressed) {
            try (FileOutputStream outputStream = new FileOutputStream(file); DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(new GZIPOutputStream(outputStream)))) {
                write(element, dataOutputStream);
            }
        } else {
            try (FileOutputStream outputStream = new FileOutputStream(file); DataOutputStream dataOutputStream = new DataOutputStream(outputStream)) {
                write(element, dataOutputStream);
            }
        }
    }

    public static void writeToFile(File file, BSOElement element, BSOCompression compression) throws IOException {
        writeToFile(file, element, ByteOrder.BIG_ENDIAN, compression);
    }

    public static void writeToFile(File file, BSOElement element, ByteOrder endianess, BSOCompression compression) throws IOException {
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            write(element, new BSOOutputStream(outputStream, endianess, compression));
        }
    }

    private static void write(BSOElement element, DataOutput outputStream) throws IOException {
        outputStream.writeByte(element.getIDAD());
        element.write(outputStream);
    }

    /**
     * Reads BSO from a {@link File}. Be careful because it may throw an exception if the top level element isn't BSOMap.
     * @param file File from which the BSO will be read from
     */
    public static BSOMap readMap(File file) throws IOException, ClassCastException {
        return (BSOMap) read(file);
    }

    /**
     * Reads compressed (GZip) BSO from a {@link File}. Be careful because it may throw an exception if the top level element isn't BSOMap.
     * @param file File from which the BSO will be read from
     */
    public static BSOMap readMapCompressed(File file) throws IOException, ClassCastException {
        return (BSOMap) readCompressed(file);
    }

    /**
     * Reads BSO from a {@link File}.
     * @param file File from which the BSO will be read from
     */
    public static BSOElement read(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file); DataInputStream dataInputStream = new DataInputStream(inputStream)) {
            return read(dataInputStream);
        }
    }

    /**
     * Reads compressed (GZip) BSO from a {@link File}.
     * @param file File from which the BSO will be read from
     */
    public static BSOElement readCompressed(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file); DataInputStream dataInputStream = new DataInputStream(new BufferedInputStream(new GZIPInputStream(inputStream)))) {
            return read(dataInputStream);
        }
    }

    public static void readFromFile(File file, BSOCompression compression) throws IOException {
        readFromFile(file, ByteOrder.BIG_ENDIAN, compression);
    }

    public static BSOElement readFromFile(File file, ByteOrder endianess, BSOCompression compression) throws IOException {
        try (FileInputStream outputStream = new FileInputStream(file)) {
            return read(new BSOInputStream(outputStream, endianess, compression));
        }
    }

    private static BSOElement read(DataInput inputStream) throws IOException {
        byte b = inputStream.readByte();
        BSOType<?> type = BSOTypes.byId((byte)(b & 0x0F));
        return type.read(inputStream, (byte) (b & 0xF0));
    }

    public enum BSOCompression {
        NONE,
        GZIP,
        ZLIB
    }
}
