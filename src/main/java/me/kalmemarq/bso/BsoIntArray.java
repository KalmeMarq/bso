package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class BsoIntArray implements BsoElement {
    private int[] array;

    public BsoIntArray(int[] array) {
        this.array = array;
    }

    public BsoIntArray(List<Integer> array) {
        this.array = new int[array.size()];
        for (int i = 0; i < array.size(); ++i) {
            this.array[i] = array.get(i);
        }
    }

    @Override
    public int getId() {
        return 0b1011;
    }

    private int getRange() {
        for (int value : this.array) {
            if (!BsoUtils.isIntInShortRange(value)) {
                return 2;
            } else if (!BsoUtils.isIntInByteRange(value)) {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int getAdditionalData() {
        int size = this.size();
        int range = this.getRange();
        return (size <= 255 ? 0b0001 : size <= 65535 ? 0b0010 : 0b0000) | (range == 0 ? 0b0100 : range == 1 ? 0b1000 : 0b0000);
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

        for (int value : this.array) {
            if (range == 0) output.writeByte(value);
            else if (range == 1) output.writeShort(value);
            else output.writeInt(value);
        }
    }

    @Override
    public void visit(BsoVisitor visitor) {
        visitor.visitIntArray(this);
    }

    public int size() {
        return this.array.length;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void clear() {
        this.array = new int[0];
    }

    public int get(int index) {
        return this.array[index];
    }

    public void add(int value) {
        this.array = Arrays.copyOf(this.array, this.array.length + 1);
        this.array[this.array.length - 1] = value;
    }

    public void set(int index, int value) {
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
        this.array = (int[]) result;
    }

    public int[] array() {
        return this.array;
    }

    @Override
    public BsoIntArray copy() {
        return new BsoIntArray(Arrays.copyOf(this.array, this.array.length));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (!(obj instanceof BsoIntArray)) return false;
        return Arrays.equals(this.array, ((BsoIntArray) obj).array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.array);
    }
}
