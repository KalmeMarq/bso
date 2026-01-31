package io.github.kalmemarq.bso;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class LittleEndianDataInput implements DataInput, AutoCloseable {
    private final DataInputStream input;

    public LittleEndianDataInput(InputStream input) {
        this.input = input instanceof DataInputStream in ? in : new DataInputStream(input);
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        this.input.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        this.input.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return this.input.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.input.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return this.input.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return this.input.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return Short.reverseBytes(this.input.readShort());
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return this.readShort() & 0xFFFF;
    }

    @Override
    public char readChar() throws IOException {
        return Character.reverseBytes(this.input.readChar());
    }

    @Override
    public int readInt() throws IOException {
        return Integer.reverseBytes(this.input.readInt());
    }

    @Override
    public long readLong() throws IOException {
        return Long.reverseBytes(this.input.readLong());
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(this.readInt());
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(this.readLong());
    }

    @Override
    public String readLine() throws IOException {
        return this.input.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return this.input.readUTF();
    }

    @Override
    public void close() throws Exception {
        this.input.close();
    }
}
