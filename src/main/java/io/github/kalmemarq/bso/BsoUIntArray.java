package io.github.kalmemarq.bso;

import java.util.Arrays;

public record BsoUIntArray(int[] values) implements BsoNode {
    @Override
    public int[] asIntArray(int[] values) {
        return this.values;
    }

    @Override
    public BsoNode copy() {
        return new BsoUIntArray(Arrays.copyOf(this.values, this.values.length));
    }
}
