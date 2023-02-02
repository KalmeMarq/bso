package me.kalmemarq.bso.number;

import java.io.DataOutput;
import java.io.IOException;

import me.kalmemarq.bso.BSOType;
import me.kalmemarq.bso.BSOTypes;

public final class BSOFloat extends AbstractBSONumber implements Comparable<BSOFloat> {
    private final float value;

    private BSOFloat(float value) {
        this.value = value;
    }

    public static BSOFloat of(float value) {
        return new BSOFloat(value);
    }

    @Override
    public BSOType<BSOFloat> getType() {
        return BSOTypes.FLOAT;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeFloat(this.value);
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
        visitor.visitFloat(this);
    }

    @Override
    public BSOFloat copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BSOFloat)) return false;
        return ((BSOFloat)obj).value == this.value;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(this.value);
    }

    @Override
    public String toString() {
        return this.asString();
    }

    @Override
    public int compareTo(BSOFloat obj) {
        return Float.compare(this.value, obj.value);
    }
}
