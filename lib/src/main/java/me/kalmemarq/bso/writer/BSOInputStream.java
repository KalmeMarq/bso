package me.kalmemarq.bso.writer;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;

import me.kalmemarq.bso.BSOIo.BSOCompression;

public class BSOInputStream extends FilterInputStream implements DataInput {
    private final ByteOrder order;

    public BSOInputStream(InputStream inputStream) throws IOException {
        this(inputStream, ByteOrder.BIG_ENDIAN, BSOCompression.NONE);
    }
    
    public BSOInputStream(InputStream inputStream, BSOCompression compression) throws IOException {
        this(inputStream, ByteOrder.BIG_ENDIAN, compression);
    }
    
    public BSOInputStream(InputStream inputStream, ByteOrder order, BSOCompression compression) throws IOException {
        this(compression == BSOCompression.GZIP ? new GZIPInputStream(inputStream) : compression == BSOCompression.ZLIB ? new InflaterInputStream(inputStream) : inputStream, order);
    }
    
    private BSOInputStream(InputStream inputStream, ByteOrder order) {
        super(inputStream instanceof DataInputStream ? inputStream : new DataInputStream(inputStream));
        this.order = order;
    }

    protected DataInputStream getStream() {
        return (DataInputStream) super.in;
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        getStream().readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        getStream().readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return getStream().skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return getStream().readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return getStream().readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return getStream().readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return order == ByteOrder.LITTLE_ENDIAN ? Short.reverseBytes(getStream().readShort()) : getStream().readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return order == ByteOrder.LITTLE_ENDIAN ? (int)((char)(Integer.reverseBytes(getStream().readUnsignedShort()) >> 16)) : getStream().readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return order == ByteOrder.LITTLE_ENDIAN ? Character.reverseBytes(getStream().readChar()) : getStream().readChar();
    }

    @Override
    public int readInt() throws IOException {
        return order == ByteOrder.LITTLE_ENDIAN ? Integer.reverseBytes(getStream().readInt()) : getStream().readInt();
    }

    @Override
    public long readLong() throws IOException {
        return order == ByteOrder.LITTLE_ENDIAN ? Long.reverseBytes(getStream().readLong()) : getStream().readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return order == ByteOrder.LITTLE_ENDIAN ? Float.intBitsToFloat(Integer.reverseBytes(readInt())) : getStream().readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return order == ByteOrder.LITTLE_ENDIAN ? Double.longBitsToDouble(Long.reverseBytes(readLong())) : getStream().readDouble();
    }

    @SuppressWarnings("deprecation")
    @Override
    public String readLine() throws IOException {
        return getStream().readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return getStream().readUTF();
    }
}
