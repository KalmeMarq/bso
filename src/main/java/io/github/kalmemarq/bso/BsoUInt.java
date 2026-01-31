package io.github.kalmemarq.bso;

public record BsoUInt(int value) implements BsoPrimitive {
    @Override
    public Number asNumber(Number defaultValue) {
        return this.value & 0xFFFFFFFFL;
    }

    @Override
    public int asInt(int defaultValue) {
        return this.value;
    }

    @Override
    public long asLong(long defaultValue) {
        return this.value & 0xFFFFFFFFL;
    }
}
