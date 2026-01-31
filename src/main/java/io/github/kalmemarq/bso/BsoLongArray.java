package io.github.kalmemarq.bso;

import java.util.Arrays;

public record BsoLongArray(long[] values) implements BsoNode {
    @Override
    public long[] asLongArray(long[] values) {
        return this.values;
    }

    @Override
    public BsoNode copy() {
        return new BsoLongArray(Arrays.copyOf(this.values, this.values.length));
    }
}
