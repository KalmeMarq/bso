package io.github.kalmemarq.bso;

public sealed interface BsoArray extends BsoNode permits BsoByteArray, BsoDoubleArray, BsoFloatArray, BsoIntArray, BsoLongArray, BsoShortArray, BsoUByteArray, BsoUIntArray, BsoULongArray, BsoUShortArray {
    @Override
    default boolean isArray() {
        return true;
    }
}
