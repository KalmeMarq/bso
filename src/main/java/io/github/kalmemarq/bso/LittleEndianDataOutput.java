package io.github.kalmemarq.bso;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LittleEndianDataOutput implements DataOutput, AutoCloseable {
    private final DataOutputStream output;

    public LittleEndianDataOutput(OutputStream output) {
        this.output = output instanceof DataOutputStream out ? out : new DataOutputStream(output);
    }

    @Override
    public void write(int b) throws IOException {
        this.output.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this.output.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        this.output.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        this.output.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        this.output.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        this.output.writeShort((v & 0xFF << 8) | (v >> 8) & 0xFF);
    }

    @Override
    public void writeChar(int v) throws IOException {
        this.output.writeChar((v & 0xFF << 8) | (v >> 8) & 0xFF);
    }

    @Override
    public void writeInt(int v) throws IOException {
        this.output.writeInt(Integer.reverseBytes(v));
    }

    @Override
    public void writeLong(long v) throws IOException {
        this.output.writeLong(Long.reverseBytes(v));
    }

    @Override
    public void writeFloat(float v) throws IOException {
        int value = Float.floatToIntBits(v);
        this.output.writeFloat(Integer.reverseBytes(value));
    }

    @Override
    public void writeDouble(double v) throws IOException {
        long value = Double.doubleToLongBits(v);
        this.output.writeLong(Long.reverseBytes(value));
    }

    @Override
    public void writeBytes(String s) throws IOException {
        this.output.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        this.output.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        this.output.writeUTF(s);
    }

    @Override
    public void close() throws IOException {
        this.output.close();
    }
}
