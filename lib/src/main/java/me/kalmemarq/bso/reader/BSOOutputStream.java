package me.kalmemarq.bso.reader;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPOutputStream;

import me.kalmemarq.bso.BSOIo.BSOCompression;

public class BSOOutputStream extends FilterOutputStream implements DataOutput {
    private final ByteOrder order;

    public BSOOutputStream(OutputStream outputStream) throws IOException {
        this(outputStream, ByteOrder.BIG_ENDIAN, BSOCompression.NONE);
    }

    public BSOOutputStream(OutputStream outputStream, BSOCompression compression) throws IOException {
        this(outputStream, ByteOrder.BIG_ENDIAN, compression);
    }

    public BSOOutputStream(OutputStream outputStream, ByteOrder order, BSOCompression compression) throws IOException {
        this(compression == BSOCompression.GZIP ? new GZIPOutputStream(outputStream) : compression == BSOCompression.ZLIB ? new DeflaterOutputStream(outputStream) : outputStream, order);
    }

    private BSOOutputStream(OutputStream outputStream, ByteOrder order) {
        super(outputStream instanceof DataOutputStream ? outputStream : new DataOutputStream(outputStream));
        this.order = order;
    }

    protected DataOutputStream getStream() {
        return (DataOutputStream) super.out;
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        getStream().writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        getStream().writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        if (order == ByteOrder.LITTLE_ENDIAN) {
            v = Integer.reverseBytes(v) >> 16;
        }
        getStream().writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        if (order == ByteOrder.LITTLE_ENDIAN) {
            v = Character.reverseBytes((char) v);
        }
        getStream().writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        if (order == ByteOrder.LITTLE_ENDIAN) {
            v = Integer.reverseBytes(v);
        }
        getStream().writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        if (order == ByteOrder.LITTLE_ENDIAN) {
            v = Long.reverseBytes(v);
        }
        getStream().writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        if (order == ByteOrder.LITTLE_ENDIAN) {
            getStream().writeInt(Integer.reverseBytes(Float.floatToIntBits(v)));
            return;
        }
        getStream().writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        if (order == ByteOrder.LITTLE_ENDIAN) {
            getStream().writeLong(Long.reverseBytes(Double.doubleToLongBits(v)));
            return;
        }
        getStream().writeDouble(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        getStream().writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        getStream().writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        getStream().writeUTF(s);
    }
}
