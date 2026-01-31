package io.github.kalmemarq.bso;

public record BsoBool(boolean value) implements BsoPrimitive {
    public static final BsoBool FALSE = new BsoBool(false);
    public static final BsoBool TRUE = new BsoBool(true);

    public static BsoBool of(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public Number asNumber(Number defaultValue) {
        return this.value ? 1 : 0;
    }

    @Override
    public boolean asBool(boolean defaultValue) {
        return this.value;
    }

    @Override
    public byte[] asByteArray(byte[] values) {
        return new byte[] {this.asByte()};
    }
}
