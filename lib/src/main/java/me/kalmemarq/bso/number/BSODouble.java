package me.kalmemarq.bso.number;

import java.io.DataOutput;
import java.io.IOException;

import me.kalmemarq.bso.BSOType;
import me.kalmemarq.bso.BSOTypes;

public final class BSODouble extends AbstractBSONumber implements Comparable<BSODouble> {
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

    @Override
    public byte asByte() {
        return (byte) (floor(this.value) & 0xFF);
    }

    @Override
    public short asShort() {
        return (short) (floor(this.value) & 0xFFFF);
    }

    @Override
    public int asInt() {
        return floor(this.value);
    }

    @Override
    public long asLong() {
        return (long) this.value;
    }

    @Override
    public float asFloat() {
        return (float) this.value;
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

    @Override
    public int compareTo(BSODouble obj) {
        return Double.compare(this.value, obj.value);
    }
}
