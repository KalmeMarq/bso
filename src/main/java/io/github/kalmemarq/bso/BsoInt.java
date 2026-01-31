package io.github.kalmemarq.bso;

public record BsoInt(int value) implements BsoPrimitive {
    @Override
    public Number asNumber(Number defaultValue) {
        return this.value;
    }

    @Override
    public int asInt(int defaultValue) {
        return this.value;
    }

    @Override
    public int[] asIntArray(int[] values) {
        return new int[]{this.value};
    }
}
