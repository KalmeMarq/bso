package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import me.kalmemarq.bso.number.BSOInt;

public final class BSOIntArray extends AbstractBSOList<BSOInt> {
    private int[] values;

    private BSOIntArray(int[] values) {
        this.values = values;
    }
    
    public static BSOIntArray of(int[] values) {
        return new BSOIntArray(values);
    }

    public static BSOIntArray of(List<Integer> values) {
        int[] vls = new int[values.size()];
        for (int i = 0; i < vls.length; i++) {
            Integer b = values.get(i);
            vls[i] = b == null ? 0 : b;
        }
        return new BSOIntArray(vls);
    }

    @Override
    public BSOType<BSOIntArray> getType() {
        return BSOTypes.INT_ARRAY;
    }
    
    @Override
    public void write(DataOutput output) throws IOException {
        if (!indefiniteLength) BSOUtil.writeLength(output, this.values.length);

        int r = this.checkRange();

        for (int i = 0; i < this.values.length; i++) {
            if (r == 2) output.writeByte((this.values[i] & 0xFF));
            else if (r == 1) output.writeShort((this.values[i] & 0xFFFF));
            else output.writeInt(this.values[i]);
        }
        if (indefiniteLength) output.writeByte(END_TYPE_ID);
    }

    @Override
    public int getAdditionalData() {
        return super.getAdditionalData() + (checkRange() * 0x40);
    }

    private int checkRange() {
        int s = 0;
        int b = 0;

        for (int i = 0; i < this.values.length; i++) {
            if (this.values[i] < s) {
                s = this.values[i];
            }

            if (this.values[i] > b) {
                b = this.values[i];
            }
        }

        if (s >= Byte.MIN_VALUE && b <= Byte.MAX_VALUE) return 2;
        if (s >= Short.MIN_VALUE && b <= Short.MAX_VALUE) return 1;
        return 0;
    }

    public int[] getIntArray() {
      return this.values;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitIntArray(this);
    }

    @Override
    public BSOElement copy() {
        int[] vls = new int[this.values.length];
        System.arraycopy(this.values, 0, vls, 0, this.values.length);
        return BSOIntArray.of(vls);
    }

    @Override
    public int size() {
        return this.values.length;
    }

    @Override
    public BSOInt set(int index, BSOInt value) {
        int b = this.values[index];
        this.values[index] = value.asInt();
        return BSOInt.of(b);
    }

    @Override
    public void add(int index, BSOInt element) {
        int[] vls = new int[this.values.length + 1];
        for (int i = 0, j = 0; i < vls.length; i++) {
            if (i != index) {
                vls[i] = this.values[j];
                ++j;
            } else {
                vls[j] = element.asInt();
            }
        }
        this.values = vls;
    }

    @Override
    public void add(BSOInt value) {
        this.add(value.asInt());
    }

    /**
     * Appends int at the end of the list.
     * @param value Value to be appended
     */
    public void add(int value) {
        int[] vls = new int[this.values.length + 1];
        System.arraycopy(this.values, 0, vls, 0, this.values.length);
        vls[vls.length - 1] = value;
        this.values = vls;
    }

    @Override
    public BSOInt remove(int index) {
        int b = this.values[index];
        int[] vls = new int[this.values.length - 1];
        for (int i = 0, j = 0; i < this.values.length; i++) {
            vls[j] = vls[i];
            if (j != index) i++;
        }
        this.values = vls;
        return BSOInt.of(b);
    }

    @Override
    public BSOInt get(int index) {
        if (index >= this.values.length || index < 0) BSOInt.of(0);
        return BSOInt.of(this.values[index]);
    }

    @Override
    public void clear() {
        this.values = new int[0];
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BSOIntArray)) return false;
        return Arrays.equals(((BSOIntArray)obj).values, this.values);
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
