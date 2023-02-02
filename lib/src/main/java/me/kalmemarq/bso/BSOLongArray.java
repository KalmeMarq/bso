package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import me.kalmemarq.bso.number.BSOLong;

public final class BSOLongArray extends AbstractBSOList<BSOLong> {
    private long[] values;

    private BSOLongArray(long[] values) {
        this.values = values;
    }
    
    public static BSOLongArray of(long[] values) {
        return new BSOLongArray(values);
    }

    public static BSOLongArray of(List<Long> values) {
        long[] vls = new long[values.size()];
        for (int i = 0; i < vls.length; i++) {
            Long b = values.get(i);
            vls[i] = b == null ? 0 : b;
        }
        return new BSOLongArray(vls);
    }

    @Override
    public BSOType<BSOLongArray> getType() {
        return BSOTypes.LONG_ARRAY;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        if (!indefiniteLength) BSOUtil.writeLength(output, this.values.length);

        boolean intRange = this.checkIntRange();

        for (int i = 0; i < this.values.length; i++) {
            if (intRange) output.writeInt((int)(this.values[i] & 0xFFFFFFFFL));
            else output.writeLong(this.values[i]);
        }
        if (indefiniteLength) output.writeByte(END_TYPE_ID);
    }

    @Override
    public int getAdditionalData() {
        return super.getAdditionalData() + (checkIntRange() ? 0x40 : 0x00);
    }

    private boolean checkIntRange() {
        long s = 0;
        long b = 0;

        for (int i = 0; i < this.values.length; i++) {
            if (this.values[i] < s) {
                s = this.values[i];
            }

            if (this.values[i] > b) {
                b = this.values[i];
            }
        }

        return s >= Integer.MIN_VALUE && b <= Integer.MAX_VALUE;
    }

    public long[] getLongArray() {
      return this.values;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitLongArray(this);
    }

    @Override
    public BSOElement copy() {
        long[] vls = new long[this.values.length];
        System.arraycopy(this.values, 0, vls, 0, this.values.length);
        return BSOLongArray.of(vls);
    }

    @Override
    public int size() {
        return this.values.length;
    }

    @Override
    public BSOLong set(int index, BSOLong value) {
        long b = this.values[index];
        this.values[index] = value.asLong();
        return BSOLong.of(b);
    }

    @Override
    public void add(int index, BSOLong element) {
        long[] vls = new long[this.values.length + 1];
        for (int i = 0, j = 0; i < vls.length; i++) {
            if (i != index) {
                vls[i] = this.values[j];
                ++j;
            } else {
                vls[j] = element.asLong();
            }
        }
        this.values = vls;
    }

    @Override
    public void add(BSOLong value) {
        this.add(value.asLong());
    }

    /**
     * Appends long at the end of the list.
     * @param value Value to be appended
     */
    public void add(long value) {
        long[] vls = new long[this.values.length + 1];
        System.arraycopy(this.values, 0, vls, 0, this.values.length);
        vls[vls.length - 1] = value;
        this.values = vls;
    }

    @Override
    public BSOLong remove(int index) {
        long b = this.values[index];
        long[] vls = new long[this.values.length - 1];
        for (int i = 0, j = 0; i < this.values.length; i++) {
            vls[j] = vls[i];
            if (j != index) i++;
        }
        this.values = vls;
        return BSOLong.of(b);
    }

    @Override
    public BSOLong get(int index) {
        if (index >= this.values.length || index < 0) BSOLong.of(0L);
        return BSOLong.of(this.values[index]);
    }

    @Override
    public void clear() {
        this.values = new long[0];
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BSOLongArray)) return false;
        return Arrays.equals(((BSOLongArray)obj).values, this.values);
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
