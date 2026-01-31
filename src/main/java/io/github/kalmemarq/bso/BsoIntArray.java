package io.github.kalmemarq.bso;

import java.util.Arrays;

public record BsoIntArray(int[] values) implements BsoNode {
    @Override
    public int[] asIntArray(int[] values) {
        return this.values;
    }

    @Override
    public BsoNode copy() {
        return new BsoIntArray(Arrays.copyOf(this.values, this.values.length));
    }
}
