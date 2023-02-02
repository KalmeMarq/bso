package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import me.kalmemarq.bso.number.BSODouble;

public final class BSODoubleArray extends AbstractBSOList<BSODouble> {
    private double[] values;

    private BSODoubleArray(double[] values) {
        this.values = values;
    }
    
    public static BSODoubleArray of(double[] values) {
        return new BSODoubleArray(values);
    }

    public static BSODoubleArray of(List<Double> values) {
        double[] vls = new double[values.size()];
        for (int i = 0; i < vls.length; i++) {
            Double b = values.get(i);
            vls[i] = b == null ? 0.0 : b;
        }
        return new BSODoubleArray(vls);
    }

    @Override
    public BSOType<BSODoubleArray> getType() {
        return BSOTypes.DOUBLE_ARRAY;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        if (!indefiniteLength) BSOUtil.writeLength(output, this.values.length);
        for (int i = 0; i < this.values.length; i++) {
            output.writeDouble(this.values[i]);
        }
        if (indefiniteLength) output.writeByte(END_TYPE_ID);
    }

    public double[] getDoubleArray() {
      return this.values;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitDoubleArray(this);
    }

    @Override
    public BSOElement copy() {
        double[] vls = new double[this.values.length];
        System.arraycopy(this.values, 0, vls, 0, this.values.length);
        return BSODoubleArray.of(vls);
    }

    @Override
    public int size() {
        return this.values.length;
    }

    @Override
    public BSODouble set(int index, BSODouble value) {
        double b = this.values[index];
        this.values[index] = value.asDouble();
        return BSODouble.of(b);
    }

    @Override
    public void add(int index, BSODouble element) {
        double[] vls = new double[this.values.length + 1];
        for (int i = 0, j = 0; i < vls.length; i++) {
            if (i != index) {
                vls[i] = this.values[j];
                ++j;
            } else {
                vls[j] = element.asDouble();
            }
        }
        this.values = vls;
    }


    @Override
    public void add(BSODouble value) {
        this.add(value.asDouble());
    }

    /**
     * Appends double at the end of the list.
     * @param value Value to be appended
     */
    public void add(double value) {
        double[] vls = new double[this.values.length + 1];
        System.arraycopy(this.values, 0, vls, 0, this.values.length);
        vls[vls.length - 1] = value;
        this.values = vls;
    }

    @Override
    public BSODouble remove(int index) {
        double b = this.values[index];
        double[] vls = new double[this.values.length - 1];
        for (int i = 0, j = 0; i < this.values.length; i++) {
            vls[j] = vls[i];
            if (j != index) i++;
        }
        this.values = vls;
        return BSODouble.of(b);
    }

    @Override
    public BSODouble get(int index) {
        if (index >= this.values.length || index < 0) return BSODouble.of(0.0d);
        return BSODouble.of(this.values[index]);
    }

    @Override
    public void clear() {
        this.values = new double[0];
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BSODoubleArray)) return false;
        return Arrays.equals(((BSODoubleArray)obj).values, this.values);
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
