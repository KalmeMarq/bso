package me.kalmemarq.bso;

import javax.xml.crypto.Data;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class EndianBasedDataInputStream extends FilterInputStream implements DataInput {
    private final BsoIo.Endianess endianess;

    public EndianBasedDataInputStream(InputStream inputStream, BsoIo.Endianess endianess) {
        super(inputStream instanceof DataInputStream ? inputStream : new DataInputStream(inputStream));
        this.endianess = endianess;
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        DataInputStream in = (DataInputStream) this.in;
        in.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        DataInputStream in = (DataInputStream) this.in;
        in.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return (int) this.in.skip(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.readUnsignedByte() != 0;
    }

    @Override
    public byte readByte() throws IOException {
        return (byte) this.readUnsignedByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        int b1 = this.in.read();
        if (0 > b1) {
            throw new EOFException();
        }
        return b1;
    }

    @Override
    public short readShort() throws IOException {
        return (short) this.readUnsignedShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        if (this.endianess == BsoIo.Endianess.LITTLE) {
            byte b1 = this.readAndCheckByte();
            byte b2 = this.readAndCheckByte();
            return (b2 & 0xFF) << 8 | (b1 & 0xFF);
        } else {
            return ((DataInputStream) this.in).readUnsignedShort();
        }
    }

    @Override
    public char readChar() throws IOException {
        return (char) this.readUnsignedShort();
    }

    @Override
    public int readInt() throws IOException {
        if (this.endianess == BsoIo.Endianess.LITTLE) {
            byte b1 = this.readAndCheckByte();
            byte b2 = this.readAndCheckByte();
            byte b3 = this.readAndCheckByte();
            byte b4 = this.readAndCheckByte();
            return b4 << 24 | (b3 & 0xFF) << 16 | (b2 & 0xFF) << 8 | (b1 & 0xFF);
        } else {
            return ((DataInputStream) this.in).readInt();
        }
    }

    @Override
    public long readLong() throws IOException {
        if (this.endianess == BsoIo.Endianess.LITTLE) {
            byte b1 = this.readAndCheckByte();
            byte b2 = this.readAndCheckByte();
            byte b3 = this.readAndCheckByte();
            byte b4 = this.readAndCheckByte();
            byte b5 = this.readAndCheckByte();
            byte b6 = this.readAndCheckByte();
            byte b7 = this.readAndCheckByte();
            byte b8 = this.readAndCheckByte();

            return (b8 & 0xFFL) << 56
                    | (b7 & 0xFFL) << 48
                    | (b6 & 0xFFL) << 40
                    | (b5 & 0xFFL) << 32
                    | (b4 & 0xFFL) << 24
                    | (b3 & 0xFFL) << 16
                    | (b2 & 0xFFL) << 8
                    | (b1 & 0xFFL);
        }
        return ((DataInputStream) this.in).readLong();
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
        throw new UnsupportedOperationException("readLine is not supported");
    }

    @Override
    public String readUTF() throws IOException {
        DataInputStream in = (DataInputStream) this.in;
        return in.readUTF();
    }

    private byte readAndCheckByte() throws IOException, EOFException {
        int b1 = this.in.read();

        if (-1 == b1) {
            throw new EOFException();
        }

        return (byte) b1;
    }
}
