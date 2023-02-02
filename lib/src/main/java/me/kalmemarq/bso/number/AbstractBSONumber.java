package me.kalmemarq.bso.number;

import me.kalmemarq.bso.BSOElement;

public abstract class AbstractBSONumber implements BSOElement {
    protected static final int VARNUM_BYTE = 0x30;
    protected static final int VARNUM_SHORT = 0x20;
    protected static final int VARNUM_INT = 0x10;

    /**
     * Returns the value as a byte.
     * @return the numeric value converted to byte
     */
    abstract byte asByte();

    /**
     * Returns the value as a short.
     * @return the numeric value converted to short
     */
    abstract short asShort();

    /**
     * Returns the value as a int.
     * @return the numeric value converted to int
     */
    abstract int asInt();

    /**
     * Returns the value as a long.
     * @return the numeric value converted to long
     */
    abstract long asLong();

    /**
     * Returns the value as a float.
     * @return the numeric value converted to float
     */
    abstract float asFloat();

    /**
     * Returns the value as a double.
     * @return the numeric value converted to double
     */
    abstract double asDouble();

    /**
     * Returns the value as {@link Number}.
     * @return the numeric value as {@link Number}
     */
    abstract Number asNumber();

    @Override
    public String toString() {
        return this.asString();
    }

    protected static int floor(float value) {
        int i = (int)value;
        return value < (float)i ? i - 1 : i;
    }

    protected static int floor(double value) {
        int i = (int)value;
        return value < (double)i ? i - 1 : i;
    }
}
