package io.github.kalmemarq.bso;

import java.util.Arrays;

public record BsoULongArray(long[] values) implements BsoNode {
    @Override
    public long[] asLongArray(long[] values) {
        return this.values;
    }

    @Override
    public BsoNode copy() {
        return new BsoULongArray(Arrays.copyOf(this.values, this.values.length));
    }
}
