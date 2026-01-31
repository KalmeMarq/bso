package io.github.kalmemarq.bso;

import java.util.Arrays;

public record BsoDoubleArray(double[] values) implements BsoNode {
    @Override
    public double[] asDoubleArray(double[] values) {
        return this.values;
    }

    @Override
    public BsoNode copy() {
        return new BsoDoubleArray(Arrays.copyOf(this.values, this.values.length));
    }
}
