package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;

public class BsoFloat implements BsoNumeric {
    private final float value;

    public BsoFloat(float value) {
        this.value = value;
    }

    @Override
    public int getId() {
        return 0b0101;
    }

    @Override
    public int getAdditionalData() {
        return 0b0000;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeFloat(this.value);
    }

    @Override
    public void visit(BsoVisitor visitor) {
        visitor.visitFloat(this);
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
        return this.value;
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
        if (!(obj instanceof BsoFloat)) return false;
        return this.value == ((BsoFloat) obj).value;
    }

    public int hashCode() {
        return Float.floatToIntBits(this.value);
    }
}
