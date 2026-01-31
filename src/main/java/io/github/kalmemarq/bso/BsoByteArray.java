package io.github.kalmemarq.bso;

import java.util.Arrays;

public record BsoByteArray(byte[] values) implements BsoNode {
    @Override
    public byte[] asByteArray(byte[] values) {
        return this.values;
    }

    @Override
    public int size() {
        return this.values.length;
    }

    @Override
    public BsoNode copy() {
        return new BsoByteArray(Arrays.copyOf(this.values, this.values.length));
    }
}
