package io.github.kalmemarq.bso;

public record BsoString(String value) implements BsoPrimitive {
    @Override
    public String asString(String defaultValue) {
        return this.value;
    }
}
