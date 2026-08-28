package io.github.kalmemarq.bso;

public record BsoString(String value) implements BsoPrimitive {
    @Override
    public String asString(String defaultValue) {
        return this.value;
    }

    @Override
    public boolean isNumber() {
        return false;
    }

    @Override
    public boolean isString() {
        return true;
    }
}
