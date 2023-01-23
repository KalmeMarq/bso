package me.kalmemarq.bso.number;

import me.kalmemarq.bso.BSOElement;

public abstract class AbstractBSONumber implements BSOElement {
    protected static final int VARNUM_BYTE = 0x30;
    protected static final int VARNUM_SHORT = 0x20;
    protected static final int VARNUM_INT = 0x10;
    
    abstract public byte byteValue();
    abstract public short shortValue();
    abstract public int intValue();
    abstract public long longValue();
    abstract public float floatValue();
    abstract public double doubleValue();
    abstract public Number numberValue();

    protected static int floor(float value) {
        int i = (int)value;
        return value < (float)i ? i - 1 : i;
    }

    protected static int floor(double value) {
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }
}
