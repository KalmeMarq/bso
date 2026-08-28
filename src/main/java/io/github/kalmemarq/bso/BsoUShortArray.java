package io.github.kalmemarq.bso;

import java.util.Arrays;

public record BsoUShortArray(short[] values) implements BsoArray {
    @Override
    public short[] asShortArray(short[] values) {
        return this.values;
    }

    @Override
    public int size() {
        return this.values.length;
    }

    @Override
    public BsoNode copy() {
        return new BsoUShortArray(Arrays.copyOf(this.values, this.values.length));
    }
}
