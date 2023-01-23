package me.kalmemarq.bso.number;

import java.io.DataOutput;
import java.io.IOException;

import me.kalmemarq.bso.BSOType;
import me.kalmemarq.bso.BSOTypes;

public final class BSOLong extends AbstractBSONumber {
    private final long value;

    private BSOLong(long value) {
        this.value = value;
    }

    public static BSOLong of(long value) {
        return Cache.get(value);
    }

    @Override
    public BSOType<BSOLong> getType() {
        return BSOTypes.LONG;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        if (this.value <= Byte.MAX_VALUE) {
            output.writeByte((int)(this.value & 0xFFL));
        } else if (this.value <= Short.MAX_VALUE) {
            output.writeShort((int)(this.value & 0xFFFFL));
        } else if (this.value <= Integer.MAX_VALUE) {
            output.writeInt((int)(this.value & 0xFFFFFFFFL));
        } else {
            output.writeLong(this.value);
        }
    }

    @Override
    public int getAdditionalData() {
        if (this.value <= Byte.MAX_VALUE) {
            return VARNUM_BYTE;
        } else if (this.value <= Short.MAX_VALUE) {
            return VARNUM_SHORT;
        } else if (this.value <= Integer.MAX_VALUE) {
            return VARNUM_INT;
        }

        return 0;
    }

    public long getValue() {
      return this.value;
    }

    @Override
    public byte byteValue() {
        return (byte) (this.value & 0xFFL);
    }

    @Override
    public short shortValue() {
        return (byte) (this.value & 0xFFFFL);
    }

    @Override
    public int intValue() {
        return (byte) (this.value & 0xFFFFFFFFL);
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
    public void accept(Visitor visitor) {
        visitor.visitLong(this);
    }

    @Override
    public BSOLong copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BSOLong)) return false;
        return ((BSOLong)obj).value == this.value;
    }

    @Override
    public int hashCode() {
        return (int)(this.value ^ this.value >>> 32);
    }

    @Override
    public String toString() {
        return this.asString();
    }

    private static class Cache {
        private static final BSOLong[] VALUES = new BSOLong[128 + 1024 - 1];
        
        private static BSOLong get(long value) {
            if (value <= -128 || value >= 1024) return new BSOLong(value);
            if (VALUES[128 + (int)value] == null) VALUES[128 + (int)value] = new BSOLong(value);
            return VALUES[128 + (int)value]; 
        }
    }
}
