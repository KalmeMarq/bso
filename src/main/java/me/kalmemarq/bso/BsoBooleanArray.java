package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class BsoBooleanArray implements BsoElement {
    private boolean[] array;

    public BsoBooleanArray(boolean[] array) {
        this.array = array;
    }

    public BsoBooleanArray(List<Byte> array) {
        this.array = new boolean[array.size()];
        for (int i = 0; i < array.size(); ++i) {
            this.array[i] = array.get(i) != 0;
        }
    }

    @Override
    public int getId() {
        return 0b1001;
    }

    @Override
    public int getAdditionalData() {
        int size = this.size();
        return size <= 255 ? 0b0101 : size <= 65535 ? 0b0110 : 0b0100;
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

        int value = 0;
        for (int i = 0; i < this.array.length; ++i) {
            int b = 3 - (i % 4);
            int v = this.array[i] ? 1 : 0;
            value = value | v << b;
            if (b == 0) {
                output.writeByte(value);
                value = 0;
            }
        }
        if (this.array.length % 4 != 0) output.writeByte(value);
    }

    @Override
    public void visit(BsoVisitor visitor) {
        visitor.visitBooleanArray(this);
    }

    public int size() {
        return this.array.length;
    }

    public void clear() {
        this.array = new boolean[0];
    }

    public boolean get(int index) {
        return this.array[index];
    }

    public void add(boolean value) {
        this.array = Arrays.copyOf(this.array, this.array.length + 1);
        this.array[this.array.length - 1] = value;
    }

    public void set(int index, boolean value) {
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
        this.array = (boolean[]) result;
    }

    public boolean[] array() {
        return this.array;
    }

    @Override
    public BsoBooleanArray copy() {
        return new BsoBooleanArray(Arrays.copyOf(this.array, this.array.length));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (!(obj instanceof BsoBooleanArray)) return false;
        return Arrays.equals(this.array, ((BsoBooleanArray) obj).array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.array);
    }
}
