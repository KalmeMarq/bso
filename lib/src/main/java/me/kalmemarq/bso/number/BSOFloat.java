package me.kalmemarq.bso.number;

import java.io.DataOutput;
import java.io.IOException;

import me.kalmemarq.bso.BSOType;
import me.kalmemarq.bso.BSOTypes;

public final class BSOFloat extends AbstractBSONumber {
    private final float value;

    private BSOFloat(float value) {
        this.value = value;
    }

    public static BSOFloat of(float value) {
        return new BSOFloat(value);
    }

    @Override
    public BSOType<BSOFloat> getType() {
        return BSOTypes.FLOAT;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeFloat(this.value);
    }

    public float getValue() {
      return this.value;
    }

    @Override
    public Number numberValue() {
        return this.value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitFloat(this);
    }

    @Override
    public BSOFloat copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BSOFloat)) return false;
        return ((BSOFloat)obj).value == this.value;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.value);
    }

    @Override
    public String toString() {
        return this.asString();
    }
}
