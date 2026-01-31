package io.github.kalmemarq.bso;

public record BsoShort(short value) implements BsoPrimitive {
    @Override
    public Number asNumber(Number defaultValue) {
        return this.value;
    }

    @Override
    public short asShort(int defaultValue) {
        return this.value;
    }

    @Override
    public short[] asShortArray(short[] values) {
        return new short[]{this.value};
    }
}
