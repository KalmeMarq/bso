package me.kalmemarq.bso.number;

import java.io.DataOutput;
import java.io.IOException;

import me.kalmemarq.bso.BSOType;
import me.kalmemarq.bso.BSOTypes;

public final class BSOByte extends AbstractBSONumber implements Comparable<BSOByte> {
    public static final BSOByte ZERO = BSOByte.of((byte)0);
    public static final BSOByte ONE = BSOByte.of((byte)1);
    
    private final byte value;

    private BSOByte(byte value) {
        this.value = value;
    }

    public static BSOByte of(int value) {
        return Cache.get((byte)value);
    }

    public static BSOByte of(byte value) {
        return Cache.get(value);
    }

    public static BSOByte of(boolean value) {
        return value ? ONE : ZERO;
    }

    @Override
    public BSOType<BSOByte> getType() {
        return BSOTypes.BYTE;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeByte(this.value);
    }

    @Override
    public byte asByte() {
        return this.value;
    }

    public int asUByte() {
        return this.value & 0xFF;
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
        visitor.visitByte(this);
    }

    @Override
    public BSOByte copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BSOByte)) return false;
        return ((BSOByte)obj).value == this.value;
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
    public int compareTo(BSOByte obj) {
        return Byte.compare(this.value, obj.value);
    }

    private static class Cache {
        private static final BSOByte[] VALUES = new BSOByte[256];
        
        private static BSOByte get(byte value) {
            if (VALUES[128 + value] == null) VALUES[128 + value] = new BSOByte(value);
            return VALUES[128 + value]; 
        }
    }
}
