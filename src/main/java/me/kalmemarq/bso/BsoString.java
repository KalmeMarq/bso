package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;

public class BsoString implements BsoElement {
    private final String value;

    public BsoString(String value) {
        this.value = value;
    }

    @Override
    public int getId() {
        return 0b0110;
    }

    @Override
    public int getAdditionalData() {
        int size = this.value.length();
        return size <= 255 ? 0b0001 : size <= 65535 ? 0b0010 : 0b0000;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        int size = this.value.length();
        BsoUtils.writeUTF(this.value, output, size <= 255 ? 0 : size <= 65535 ? 1 : 2, false);
    }

    @Override
    public void visit(BsoVisitor visitor) {
        visitor.visitString(this);
    }

    public String value() {
        return this.value;
    }

    @Override
    public String asString() {
        return this.value;
    }

    @Override
    public String toString() {
        return BsoElement.super.asString();
    }

    @Override
    public BsoElement copy() {
        return this;
    }
}
