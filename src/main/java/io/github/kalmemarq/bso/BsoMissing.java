package io.github.kalmemarq.bso;

public record BsoMissing() implements BsoNode {
    public static final BsoMissing INSTANCE = new BsoMissing();

    @Override
    public BsoNode copy() {
        return this;
    }
}
