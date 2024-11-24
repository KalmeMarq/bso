package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;

public class BsoShort implements BsoNumeric {
    private final short value;

    public BsoShort(short value) {
        this.value = value;
    }

    @Override
    public int getId() {
        return 0b0010;
    }

    @Override
    public int getAdditionalData() {
        return BsoUtils.isShortInByteRange(this.value) ? 0b0001 : 0b0000;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        if (BsoUtils.isShortInByteRange(this.value)) {
            output.writeByte(this.byteValue());
        } else {
            output.writeShort(this.value);
        }
    }

    @Override
    public void visit(BsoVisitor visitor) {
        visitor.visitShort(this);
    }

    @Override
    public byte byteValue() {
        return (byte)(this.value & 0xFF);
    }

    @Override
    public short shortValue() {
        return this.value;
    }

    @Override
    public int intValue() {
        return this.value;
    }

    @Override
    public long longValue() {
        return this.value;
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
        if (!(obj instanceof BsoShort)) return false;
        return this.value == ((BsoShort) obj).value;
    }

    public int hashCode() {
        return this.value;
    }
}
