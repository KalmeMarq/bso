package io.github.kalmemarq.bso;

public record BsoLong(long value) implements BsoPrimitive {
    @Override
    public Number asNumber(Number defaultValue) {
        return this.value;
    }

    @Override
    public long asLong(long defaultValue) {
        return this.value;
    }

    @Override
    public long[] asLongArray(long[] values) {
        return new long[]{this.value};
    }
}
