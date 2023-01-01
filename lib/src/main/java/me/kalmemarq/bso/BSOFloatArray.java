package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import me.kalmemarq.bso.number.BSOFloat;

public final class BSOFloatArray extends AbstractBSOList<BSOFloat> {
    private float[] values;

    private BSOFloatArray(float[] values) {
        this.values = values;
    }
    
    public static BSOFloatArray of(float[] values) {
        return new BSOFloatArray(values);
    }

    public static BSOFloatArray of(List<Float> values) {
        float[] vls = new float[values.size()];
        for (int i = 0; i < vls.length; i++) {
            Float b = values.get(i);
            vls[i] = b == null ? 0.0f : b;
        }
        return new BSOFloatArray(vls);
    }

    @Override
    public BSOType<BSOFloatArray> getType() {
        return BSOTypes.FLOAT_ARRAY;
    }
    
    @Override
    public byte getHeldTypeId() {
        return BSOTypes.FLOAT.getId();
    }

    @Override
    public void write(DataOutput output) throws IOException {
        if (!indefiniteLength) BSOUtils.writeLength(output, this.values.length);
        for (int i = 0; i < this.values.length; i++) {
            output.writeFloat(this.values[i]);
        }
        if (indefiniteLength) output.writeByte(END_TYPE_ID);
    }

    public float[] getFloatArray() {
      return this.values;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitFloatArray(this);
    }

    @Override
    public BSOElement copy() {
        float[] vls = new float[this.values.length];
        System.arraycopy(this.values, 0, vls, 0, this.values.length);
        return BSOFloatArray.of(vls);
    }

    @Override
    public int size() {
        return this.values.length;
    }

    @Override
    public BSOFloat set(int index, BSOFloat value) {
        float b = this.values[index];
        this.values[index] = value.getValue();
        return BSOFloat.of(b);
    }

    @Override
    public void add(int index, BSOFloat element) {
        float[] vls = new float[this.values.length + 1];
        for (int i = 0, j = 0; i < vls.length; i++) {
            if (i != index) {
                vls[i] = this.values[j];
                ++j;
            } else {
                vls[j] = element.getValue();
            }
        }
        this.values = vls;
    }

    @Override
    public BSOFloat remove(int index) {
        float b = this.values[index];
        float[] vls = new float[this.values.length - 1];
        for (int i = 0, j = 0; i < this.values.length; i++) {
            vls[j] = vls[i];
            if (j != index) i++;
        }
        this.values = vls;
        return BSOFloat.of(b);
    }

    @Override
    public BSOFloat get(int index) {
        return BSOFloat.of(this.values[index]);
    }

    @Override
    public void clear() {
        this.values = new float[0];
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BSOFloatArray)) return false;
        return Arrays.equals(((BSOFloatArray)obj).values, this.values);
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
