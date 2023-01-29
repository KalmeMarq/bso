package me.kalmemarq.bso.number;

import java.io.DataOutput;
import java.io.IOException;

import me.kalmemarq.bso.BSOType;
import me.kalmemarq.bso.BSOTypes;

public final class BSOShort extends AbstractBSONumber {
    private final short value;

    private BSOShort(short value) {
        this.value = value;
    }

    public static BSOShort of(int value) {
        return Cache.get((short)value);
    }

    public static BSOShort of(short value) {
        return Cache.get(value);
    }

    @Override
    public BSOType<BSOShort> getType() {
        return BSOTypes.SHORT;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeShort(this.value);
    }

    public short getValue() {
      return this.value;
    }

    @Override
    public byte byteValue() {
        return (byte) (this.value & 0xFF);
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
    public void accept(Visitor visitor) {
        visitor.visitShort(this);
    }

    @Override
    public BSOShort copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BSOShort)) return false;
        return ((BSOShort)obj).value == this.value;
    }

    @Override
    public int hashCode() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.asString();
    }

    private static class Cache {
        private static final BSOShort[] VALUES = new BSOShort[128 + 1024 - 1];
        
        private static BSOShort get(short value) {
            if (value <= -128 || value >= 1024) return new BSOShort(value);
            if (VALUES[128 + value] == null) VALUES[128 + value] = new BSOShort(value);
            return VALUES[128 + value]; 
        }
    }
}
