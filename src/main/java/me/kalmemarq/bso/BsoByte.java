package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;

public class BsoByte implements BsoNumeric {
    protected final byte value;

    public BsoByte(byte value) {
        this.value = value;
    }

    @Override
    public int getId() {
        return 0b0001;
    }

    @Override
    public int getAdditionalData() {
        return 0b0000;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeByte(this.value);
    }

    @Override
    public void visit(BsoVisitor visitor) {
        visitor.visitByte(this);
    }

    @Override
    public byte byteValue() {
        return this.value;
    }

    @Override
    public short shortValue() {
        return this.value;
    }

    @Override
    public int intValue() {
        return this.value;
    }

    @Override
    public long longValue() {
        return this.value;
    }

    @Override
    public float floatValue() {
        return this.value;
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    @Override
    public Number numberValue() {
        return this.value;
    }

    @Override
    public BsoByte copy() {
        return this;
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BsoByte)) return false;
        return this.value == ((BsoByte) obj).value;
    }

    public int hashCode() {
        return this.value;
    }
}
