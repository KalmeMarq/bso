package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import me.kalmemarq.bso.number.BSOShort;

public final class BSOShortArray extends AbstractBSOList<BSOShort> {
    private short[] values;

    public BSOShortArray(short[] values) {
        this.values = values;
    }
    
    public static BSOShortArray of(short[] values) {
        return new BSOShortArray(values);
    }

    public static BSOShortArray of(List<Short> values) {
        short[] vls = new short[values.size()];
        for (int i = 0; i < vls.length; i++) {
            Short b = values.get(i);
            vls[i] = b == null ? (short) 0 : b;
        }
        return new BSOShortArray(vls);
    }

    @Override
    public BSOType<BSOShortArray> getType() {
        return BSOTypes.SHORT_ARRAY;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        if (!indefiniteLength) BSOUtil.writeLength(output, this.values.length);
        for (int i = 0; i < this.values.length; i++) {
            output.writeShort(this.values[i]);
        }
        if (indefiniteLength) output.writeByte(END_TYPE_ID);
    }

    public short[] getShortArray() {
      return this.values;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitShortArray(this);
    }

    @Override
    public BSOElement copy() {
        short[] vls = new short[this.values.length];
        System.arraycopy(this.values, 0, vls, 0, this.values.length);
        return BSOShortArray.of(vls);
    }

    @Override
    public int size() {
        return this.values.length;
    }

    @Override
    public BSOShort set(int index, BSOShort value) {
        short b = this.values[index];
        this.values[index] = value.asShort();
        return BSOShort.of(b);
    }

    @Override
    public void add(int index, BSOShort element) {
        short[] vls = new short[this.values.length + 1];
        for (int i = 0, j = 0; i < vls.length; i++) {
            if (i != index) {
                vls[i] = this.values[j];
                ++j;
            } else {
                vls[j] = element.asShort();
            }
        }
        this.values = vls;
    }

    @Override
    public void add(BSOShort value) {
        this.add(value.asShort());
    }

    /**
     * Appends short at the end of the list.
     * @param value Value to be appended
     */
    public void add(short value) {
        short[] vls = new short[this.values.length + 1];
        System.arraycopy(this.values, 0, vls, 0, this.values.length);
        vls[vls.length - 1] = value;
        this.values = vls;
    }

    @Override
    public BSOShort remove(int index) {
        short b = this.values[index];
        short[] vls = new short[this.values.length - 1];
        for (int i = 0, j = 0; i < this.values.length; i++) {
            vls[j] = vls[i];
            if (j != index) i++;
        }
        this.values = vls;
        return BSOShort.of(b);
    }

    @Override
    public BSOShort get(int index) {
        if (index >= this.values.length || index < 0) BSOShort.of(0);
        return BSOShort.of(this.values[index]);
    }

    @Override
    public void clear() {
        this.values = new short[0];
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BSOShortArray)) return false;
        return Arrays.equals(((BSOShortArray)obj).values, this.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }

    @Override
    public String toString() {
        return this.asString();
    }
}
