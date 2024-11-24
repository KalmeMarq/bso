package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;

public class BsoLong implements BsoNumeric {
    private final long value;

    public BsoLong(long value) {
        this.value = value;
    }

    @Override
    public int getId() {
        return 0b0100;
    }

    @Override
    public int getAdditionalData() {
        return BsoUtils.isLongInByteRange(this.value) ? 0b0001
                : BsoUtils.isLongInShortRange(this.value) ? 0b0010
                : BsoUtils.isLongInIntRange(this.value) ? 0b0011 : 0b0000;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        if (BsoUtils.isLongInByteRange(this.value)) {
            output.writeByte(this.byteValue());
        } else if (BsoUtils.isLongInShortRange(this.value)) {
            output.writeShort(this.shortValue());
        } else if (BsoUtils.isLongInIntRange(this.value)) {
            output.writeInt(this.intValue());
        } else {
            output.writeLong(this.value);
        }
    }

    @Override
    public void visit(BsoVisitor visitor) {
        visitor.visitLong(this);
    }

    @Override
    public byte byteValue() {
        return (byte) (this.value & 0xFFL);
    }

    @Override
    public short shortValue() {
        return (short) (this.value & 0xFFFFL);
    }

    @Override
    public int intValue() {
        return (int) (this.value);
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
    public BsoLong copy() {
        return this;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BsoLong)) return false;
        return this.value == ((BsoLong) obj).value;
    }

    public int hashCode() {
        return Long.hashCode(this.value);
    }
}
