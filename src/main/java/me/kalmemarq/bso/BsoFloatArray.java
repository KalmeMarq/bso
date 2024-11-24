package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public class BsoFloatArray implements BsoElement {
    private float[] array;

    public BsoFloatArray(float[] array) {
        this.array = array;
    }

    public BsoFloatArray(List<Float> array) {
        this.array = new float[array.size()];
        for (int i = 0; i < array.size(); ++i) {
            this.array[i] = array.get(i);
        }
    }

    @Override
    public int getId() {
        return 0b1101;
    }

    @Override
    public int getAdditionalData() {
        int size = this.size();
        return (size <= 255 ? 0b0001 : size <= 65535 ? 0b0010 : 0b0000);
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
        for (float value : this.array) {
            output.writeFloat(value);
        }
    }

    @Override
    public void visit(BsoVisitor visitor) {
        visitor.visitFloatArray(this);
    }

    public int size() {
        return this.array.length;
    }

    public boolean isEmpty() {
        return this.size() == 0;
    }

    public void clear() {
        this.array = new float[0];
    }

    public float get(int index) {
        return this.array[index];
    }

    public void add(float value) {
        this.array = Arrays.copyOf(this.array, this.array.length + 1);
        this.array[this.array.length - 1] = value;
    }

    public void set(int index, float value) {
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
        this.array = (float[]) result;
    }

    public float[] array() {
        return this.array;
    }

    @Override
    public BsoFloatArray copy() {
        return new BsoFloatArray(Arrays.copyOf(this.array, this.array.length));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        else if (!(obj instanceof BsoFloatArray)) return false;
        return Arrays.equals(this.array, ((BsoFloatArray) obj).array);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.array);
    }
}
