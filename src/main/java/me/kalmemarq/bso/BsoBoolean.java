package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;

public class BsoBoolean extends BsoByte {
    public static final BsoBoolean FALSE = new BsoBoolean(false);
    public static final BsoBoolean TRUE = new BsoBoolean(true);

    private BsoBoolean(boolean value) {
        super((byte) (value ? 1 : 0));
    }

    public static BsoBoolean of(boolean value) {
        return value ? TRUE : FALSE;
    }

    @Override
    public int getAdditionalData() {
        return this.value == 0 ? 0b0010 : 0b0001;
    }

    @Override
    public void write(DataOutput output) throws IOException {
    }

    @Override
    public void visit(BsoVisitor visitor) {
        visitor.visitBoolean(this);
    }

    @Override
    public BsoByte copy() {
        return this.booleanValue() ? TRUE : FALSE;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BsoBoolean)) return false;
        return this.value == ((BsoBoolean) obj).value;
    }
}
