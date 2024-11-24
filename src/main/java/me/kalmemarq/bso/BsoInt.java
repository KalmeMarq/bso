package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;

public class BsoInt implements BsoNumeric {
    private final int value;

    public BsoInt(int value) {
        this.value = value;
    }

    @Override
    public int getId() {
        return 0b0011;
    }

    @Override
    public int getAdditionalData() {
        return BsoUtils.isIntInByteRange(this.value) ? 0b0001 : BsoUtils.isIntInShortRange(this.value) ? 0b0010 : 0b0000;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        if (BsoUtils.isIntInByteRange(this.value)) {
            output.writeByte(this.byteValue());
        } else if (BsoUtils.isIntInShortRange(this.value)) {
            output.writeShort(this.shortValue());
        } else {
            output.writeInt(this.value);
        }
    }

    @Override
    public void visit(BsoVisitor visitor) {
        visitor.visitInt(this);
    }

    @Override
    public byte byteValue() {
        return (byte) (this.value & 0xFF);
    }

    @Override
    public short shortValue() {
        return (short) (this.value & 0xFFFF);
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
    public BsoInt copy() {
        return this;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BsoInt)) return false;
        return this.value == ((BsoInt) obj).value;
    }

    public int hashCode() {
        return this.value;
    }
}
