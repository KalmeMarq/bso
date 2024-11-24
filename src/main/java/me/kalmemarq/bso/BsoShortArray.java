package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class BsoShortArray implements BsoElement {
    private short[] array;

    public BsoShortArray(short[] array) {
        this.array = array;
    }

    public BsoShortArray(List<Short> array) {
        this.array = new short[array.size()];
        for (int i = 0; i < array.size(); ++i) {
            this.array[i] = array.get(i);
        }
    }

    @Override
    public int getId() {
        return 0b1010;
    }

    private int getRange() {
        for (short value : this.array) {
            if (!BsoUtils.isShortInByteRange(value)) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int getAdditionalData() {
        int size = this.size();
        int range = this.getRange();
        return (size <= 255 ? 0b0001 : size <= 65535 ? 0b0010 : 0b0000) | (range == 0 ? 0b0100 : 0b0000);
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
        int range = this.getRange();

        for (short value : this.array) {
            if (range == 0) output.writeByte(value);
            else output.writeShort(value);
        }
    }

    @Override
    public void visit(BsoVisitor visitor) {
        visitor.visitShortArray(this);
    }

    public int size() {
        return this.array.length;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void clear() {
        this.array = new short[0];
    }

    public short get(int index) {
        return this.array[index];
    }

    public void add(short value) {
        this.array = Arrays.copyOf(this.array, this.array.length + 1);
        this.array[this.array.length - 1] = value;
    }

    public void set(int index, short value) {
        this.array[index] = value;
    }

    public void pop() {
        this.remove(this.array.length - 1);
    }

    public void remove(int index) {
        Object result = Array.newInstance(array.getClass().getComponentType(), this.array.length - 1);
        System.arraycopy(this.array, 0, result, 0, index);
        if (index < this.array.length - 1) {
            System.arraycopy(this.array, index + 1, result, index, this.array.length - index - 1);
        }
        this.array = (short[]) result;
    }

    public short[] array() {
        return this.array;
    }

    @Override
    public BsoShortArray copy() {
        return new BsoShortArray(Arrays.copyOf(this.array, this.array.length));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (!(obj instanceof BsoShortArray)) return false;
        return Arrays.equals(this.array, ((BsoShortArray) obj).array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.array);
    }
}
