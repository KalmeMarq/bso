package io.github.kalmemarq.bso;

public sealed interface BsoPrimitive extends BsoNode permits BsoBool, BsoByte, BsoDouble, BsoFloat, BsoInt, BsoLong, BsoShort, BsoString, BsoUByte, BsoUInt, BsoULong, BsoUShort {
    @Override
    default BsoNode copy() {
        return this;
    }
}
