package io.github.kalmemarq.bso;

public record BsoUByte(byte value) implements BsoPrimitive {
    @Override
    public Number asNumber(Number defaultValue) {
        return this.value & 0xFF;
    }

    @Override
    public byte asByte(int defaultValue) {
        return this.value;
    }

    @Override
    public short asShort(int defaultValue) {
        return (short) (this.value & 0xFF);
    }

    @Override
    public int asInt(int defaultValue) {
        return this.value & 0xFF;
    }
}
