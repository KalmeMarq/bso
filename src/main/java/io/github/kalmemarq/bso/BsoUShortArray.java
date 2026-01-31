package io.github.kalmemarq.bso;

import java.util.Arrays;

public record BsoUShortArray(short[] values) implements BsoNode {
    @Override
    public short[] asShortArray(short[] values) {
        return this.values;
    }

    @Override
    public BsoNode copy() {
        return new BsoUShortArray(Arrays.copyOf(this.values, this.values.length));
    }
}
