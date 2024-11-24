package me.kalmemarq.bso;

public interface BsoNumeric extends BsoElement {
    byte byteValue();
    short shortValue();

    default boolean booleanValue() {
        return this.byteValue() != 0;
    }

    int intValue();
    long longValue();
    float floatValue();
    double doubleValue();
    Number numberValue();
}
