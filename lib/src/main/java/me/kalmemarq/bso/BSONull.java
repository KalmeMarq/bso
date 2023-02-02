package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;

public final class BSONull implements BSOElement {
    public static final BSONull INSTANCE = new BSONull();

    @Override
    public BSOType<?> getType() {
        return BSOTypes.NULL;
    }

    @Override
    public void write(DataOutput output) throws IOException {
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitNull(this);
    }

    @Override
    public BSOElement copy() {
        return INSTANCE;
    }
}
