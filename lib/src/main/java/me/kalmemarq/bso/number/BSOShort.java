package me.kalmemarq.bso.number;

import java.io.DataOutput;
import java.io.IOException;

import me.kalmemarq.bso.BSOType;
import me.kalmemarq.bso.BSOTypes;

public final class BSOShort extends AbstractBSONumber implements Comparable<BSOShort> {
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
        if (this.value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) output.writeByte((byte)(this.value & 0xFF));
        else output.writeShort(this.value);
    }

    @Override
    public int getAdditionalData() {
        return this.value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE ? VARNUM_BYTE : 0x00;
    }

    @Override
    public byte asByte() {
        return (byte) (this.value & 0xFF);
    }

    @Override
    public short asShort() {
        return this.value;
    }

    @Override
    public int asInt() {
        return this.value;
    }

    @Override
    public long asLong() {
        return this.value;
    }

    @Override
    public float asFloat() {
        return this.value;
    }

    @Override
    public double asDouble() {
        return this.value;
    }

    @Override
    public Number asNumber() {
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

    @Override
    public int compareTo(BSOShort obj) {
        return Short.compare(this.value, obj.value);
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
