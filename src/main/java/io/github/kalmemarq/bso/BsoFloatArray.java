package io.github.kalmemarq.bso;

import java.util.Arrays;

public record BsoFloatArray(float[] values) implements BsoNode {
    @Override
    public float[] asFloatArray(float[] values) {
        return this.values;
    }

    @Override
    public BsoNode copy() {
        return new BsoFloatArray(Arrays.copyOf(this.values, this.values.length));
    }
}
