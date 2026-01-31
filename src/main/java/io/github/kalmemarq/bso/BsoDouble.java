package io.github.kalmemarq.bso;

public record BsoDouble(double value) implements BsoPrimitive {
    @Override
    public Number asNumber(Number defaultValue) {
        return this.value;
    }

    @Override
    public double asDouble(double defaultValue) {
        return this.value;
    }

    @Override
    public double[] asDoubleArray(double[] values) {
        return new double[]{this.value};
    }
}
