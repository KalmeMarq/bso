package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class EndianBasedDataOutputStream extends FilterOutputStream implements DataOutput {
    private final BsoIo.Endianess endianess;

    public EndianBasedDataOutputStream(OutputStream outputStream, BsoIo.Endianess endianess) {
        super(outputStream instanceof DataOutputStream ? outputStream : new DataOutputStream(outputStream));
        this.endianess = endianess;
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        DataOutputStream out = (DataOutputStream) this.out;
        out.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        DataOutputStream out = (DataOutputStream) this.out;
        out.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        DataOutputStream out = (DataOutputStream) this.out;
        if (this.endianess == BsoIo.Endianess.LITTLE) {
            out.write(0xFF & v);
            out.write(0xFF & (v >> 8));
        } else {
            out.writeShort(v);
        }
    }

    @Override
    public void writeChar(int v) throws IOException {
        this.writeShort(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        DataOutputStream out = (DataOutputStream) this.out;
        if (this.endianess == BsoIo.Endianess.LITTLE) {
            out.write(0xFF & v);
            out.write(0xFF & (v >> 8));
            out.write(0xFF & (v >> 16));
            out.write(0xFF & (v >> 24));
        } else {
            out.writeInt(v);
        }
    }

    @Override
    public void writeLong(long v) throws IOException {
        DataOutputStream out = (DataOutputStream) this.out;
        if (this.endianess == BsoIo.Endianess.LITTLE) out.writeLong(Long.reverseBytes(v));
        else out.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        DataOutputStream out = (DataOutputStream) this.out;
        if (this.endianess == BsoIo.Endianess.LITTLE) this.writeInt(Float.floatToIntBits(v));
        else out.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        DataOutputStream out = (DataOutputStream) this.out;
        if (this.endianess == BsoIo.Endianess.LITTLE) this.writeLong(Double.doubleToLongBits(v));
        else out.writeDouble(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        DataOutputStream out = (DataOutputStream) this.out;
        out.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        DataOutputStream out = (DataOutputStream) this.out;
        if (this.endianess == BsoIo.Endianess.LITTLE) {
            for (int i = 0; i < s.length(); i++) {
                this.writeChar(s.charAt(i));
            }
        } else {
            out.writeChars(s);
        }
    }

    @Override
    public void writeUTF(String s) throws IOException {
        DataOutputStream out = (DataOutputStream) this.out;
        out.writeUTF(s);
    }

    @Override
    public void close() throws IOException {
        this.out.close();
    }
}
