package me.kalmemarq.bso.number;

import java.io.DataOutput;
import java.io.IOException;

import me.kalmemarq.bso.BSOType;
import me.kalmemarq.bso.BSOTypes;

public final class BSODouble extends AbstractBSONumber {
    private final double value;

    private BSODouble(double value) {
        this.value = value;
    }

    public static BSODouble of(double value) {
        return new BSODouble(value);
    }

    @Override
    public BSOType<BSODouble> getType() {
        return BSOTypes.DOUBLE;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeDouble(this.value);
    }

    public double getValue() {
      return this.value;
    }

    @Override
    public Number numberValue() {
        return this.value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitDouble(this);
    }

    @Override
    public BSODouble copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BSODouble)) return false;
        return ((BSODouble)obj).value == this.value;
    }

    @Override
    public int hashCode() {
        long l = Double.doubleToLongBits(this.value);
        return (int) (l ^ l >>> 32);
    }

    @Override
    public String toString() {
        return this.asString();
    }
}
