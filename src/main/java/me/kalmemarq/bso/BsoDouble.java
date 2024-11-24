package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;

public class BsoDouble implements BsoNumeric {
    private final double value;

    public BsoDouble(double value) {
        this.value = value;
    }

    @Override
    public int getId() {
        return 0b0101;
    }

    @Override
    public int getAdditionalData() {
        return 0b0001;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeDouble(this.value);
    }

    @Override
    public void visit(BsoVisitor visitor) {
        visitor.visitDouble(this);
    }

    @Override
    public byte byteValue() {
        return (byte) ((int) Math.floor(this.value) & 0xFF);
    }

    @Override
    public short shortValue() {
        return (short) ((int) Math.floor(this.value) & 0xFFFF);
    }

    @Override
    public int intValue() {
        return (int) Math.floor(this.value);
    }

    @Override
    public long longValue() {
        return (long) this.value;
    }

    @Override
    public float floatValue() {
        return (float) this.value;
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    @Override
    public Number numberValue() {
        return this.value;
    }

    @Override
    public BsoElement copy() {
        return this;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BsoDouble)) return false;
        return this.value == ((BsoDouble) obj).value;
    }

    public int hashCode() {
        return Double.hashCode(this.value);
    }
}
