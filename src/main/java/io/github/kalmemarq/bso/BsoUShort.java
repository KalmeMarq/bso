package io.github.kalmemarq.bso;

public record BsoUShort(short value) implements BsoPrimitive {
    @Override
    public Number asNumber(Number defaultValue) {
        return this.value;
    }

    @Override
    public short asShort(int defaultValue) {
        return this.value;
    }

    @Override
    public int asInt(int defaultValue) {
        return this.value & 0xFFFF;
    }
}
