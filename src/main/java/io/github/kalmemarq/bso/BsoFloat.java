package io.github.kalmemarq.bso;

public record BsoFloat(float value) implements BsoPrimitive {
    @Override
    public Number asNumber(Number defaultValue) {
        return this.value;
    }

    @Override
    public float asFloat(float defaultValue) {
        return this.value;
    }

    @Override
    public float[] asFloatArray(float[] values) {
        return new float[]{this.value};
    }
}
