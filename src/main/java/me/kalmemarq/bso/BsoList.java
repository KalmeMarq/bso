package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class BsoList implements BsoElement, Iterable<BsoElement> {
    private final List<BsoElement> list;

    public BsoList() {
        this(new ArrayList<>());
    }

    public BsoList(List<BsoElement> list) {
        this.list = list;
    }

    @Override
    public int getId() {
        return 0b1000;
    }

    @Override
    public int getAdditionalData() {
        int size = this.list.size();
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

        for (BsoElement value : this.list) {
            output.writeByte(value.getId() | value.getAdditionalData() << 4);
            value.write(output);
        }
    }

    @Override
    public void visit(BsoVisitor visitor) {
        visitor.visitList(this);
    }

    public int size() {
        return this.list.size();
    }

    public void clear() {
        this.list.clear();
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public void add(BsoElement element) {
        this.list.add(element);
    }

    public void add(byte value) {
        this.list.add(new BsoByte(value));
    }

    public void add(boolean value) {
        this.list.add(BsoBoolean.of(value));
    }

    public void add(short value) {
        this.list.add(new BsoShort(value));
    }

    public void add(int value) {
        this.list.add(new BsoInt(value));
    }

    public void add(long value) {
        this.list.add(new BsoLong(value));
    }

    public void add(float value) {
        this.list.add(new BsoFloat(value));
    }

    public void add(double value) {
        this.list.add(new BsoDouble(value));
    }

    public void add(String value) {
        this.list.add(new BsoString(value));
    }

    public void add(byte[] value) {
        this.list.add(new BsoByteArray(value));
    }

    public void add(boolean[] value) {
        this.list.add(new BsoBooleanArray(value));
    }

    public void add(short[] value) {
        this.list.add(new BsoShortArray(value));
    }

    public void add(int[] value) {
        this.list.add(new BsoIntArray(value));
    }

    public void add(long[] value) {
        this.list.add(new BsoLongArray(value));
    }

    public void add(float[] value) {
        this.list.add(new BsoFloatArray(value));
    }

    public void add(double[] value) {
        this.list.add(new BsoDoubleArray(value));
    }

    public BsoElement get(int index) {
        return this.list.get(index);
    }

    @Override
    public Iterator<BsoElement> iterator() {
        return this.list.iterator();
    }

    @Override
    public BsoElement copy() {
        List<BsoElement> list = new ArrayList<>(this.list.size());
        for (BsoElement element : this.list) {
            list.add(element.copy());
        }
        return new BsoList(list);
    }

    @Override
    public String toString() {
        return this.asString();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BsoList)) return false;
        return Objects.equals(this.list, ((BsoList) obj).list);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }
}
