package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class BsoByteArray implements BsoElement {
    private byte[] array;

    public BsoByteArray(byte[] array) {
        this.array = array;
    }

    public BsoByteArray(List<Byte> array) {
        this.array = new byte[array.size()];
        for (int i = 0; i < array.size(); ++i) {
            this.array[i] = array.get(i);
        }
    }

    @Override
    public int getId() {
        return 0b1001;
    }

    @Override
    public int getAdditionalData() {
        int size = this.size();
        return size <= 255 ? 0b0001 : size <= 65535 ? 0b0010 : 0b0000;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        int size = this.size();
        if (size <= 255) {
            output.writeByte((byte) size);
        } else if (size <= 65535) {
            output.writeShort((short) size);
        } else {
            output.writeInt(size);
        }

        output.write(this.array);
    }

    @Override
    public void visit(BsoVisitor visitor) {
        visitor.visitByteArray(this);
    }

    public int size() {
        return this.array.length;
    }

    public void clear() {
        this.array = new byte[0];
    }

    public byte get(int index) {
        return this.array[index];
    }

    public void add(byte value) {
        this.array = Arrays.copyOf(this.array, this.array.length + 1);
        this.array[this.array.length - 1] = value;
    }

    public void set(int index, byte value) {
        this.array[index] = value;
    }

    public void pop() {
        this.remove(this.array.length - 1);
    }

    public void remove(int index) {
        Object result = Array.newInstance(this.array.getClass().getComponentType(), this.array.length - 1);
        System.arraycopy(this.array, 0, result, 0, index);
        if (index < this.array.length - 1) {
            System.arraycopy(this.array, index + 1, result, index, this.array.length - index - 1);
        }
        this.array = (byte[]) result;
    }

    public byte[] array() {
        return this.array;
    }

    @Override
    public BsoByteArray copy() {
        return new BsoByteArray(Arrays.copyOf(this.array, this.array.length));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (!(obj instanceof BsoByteArray)) return false;
        return Arrays.equals(this.array, ((BsoByteArray) obj).array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.array);
    }
}
