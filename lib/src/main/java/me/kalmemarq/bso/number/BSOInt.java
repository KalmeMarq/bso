package me.kalmemarq.bso.number;

import java.io.DataOutput;
import java.io.IOException;

import me.kalmemarq.bso.BSOType;
import me.kalmemarq.bso.BSOTypes;
import me.kalmemarq.bso.BSOUtils;

public final class BSOInt extends AbstractBSONumber {
    private final int value;

    private BSOInt(int value) {
        this.value = value;
    }

    public static BSOInt of(int value) {
        return Cache.get(value);
    }

    @Override
    public BSOType<BSOInt> getType() {
        return BSOTypes.INT;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        if (this.value <= Byte.MAX_VALUE) {
            output.writeByte((int)(this.value & 0xFF));
        } else if (this.value <= Short.MAX_VALUE) {
            output.writeShort((int)(this.value & 0xFFFF));
        } else {
            output.writeInt(this.value);
        }
    }

    @Override
    public int getAdditionalData() {
        if (this.value <= Byte.MAX_VALUE) {
            return VARNUM_BYTE;
        } else if (this.value <= Short.MAX_VALUE) {
            return VARNUM_SHORT;
        }

        return 0;
    }

    public int getValue() {
      return this.value;
    }

    @Override
    public Number numberValue() {
        return this.value;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitInt(this);
    }

    @Override
    public BSOInt copy() {
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BSOInt)) return false;
        return ((BSOInt)obj).value == this.value;
    }

    @Override
    public int hashCode() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.asString();
    }

    private static class Cache {
        private static final BSOInt[] VALUES = new BSOInt[128 + 1024 - 1];
        
        private static BSOInt get(int value) {
            if (value <= -128 || value >= 1024) return new BSOInt(value);
            if (VALUES[128 + value] == null) VALUES[128 + value] = new BSOInt(value);
            return VALUES[128 + value]; 
        }
    }
}
