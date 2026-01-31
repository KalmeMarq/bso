package io.github.kalmemarq.bso;

public record BsoULong(long value) implements BsoPrimitive {
    @Override
    public Number asNumber(Number defaultValue) {
        return this.value;
    }

    @Override
    public long asLong(long defaultValue) {
        return this.value;
    }
}
