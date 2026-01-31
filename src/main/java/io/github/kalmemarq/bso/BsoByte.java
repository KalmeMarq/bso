package io.github.kalmemarq.bso;

public record BsoByte(byte value) implements BsoPrimitive {
    @Override
    public Number asNumber(Number defaultValue) {
        return this.value;
    }

    @Override
    public byte asByte(int defaultValue) {
        return this.value;
    }

    @Override
    public byte[] asByteArray(byte[] values) {
        return new byte[]{this.value};
    }
}
