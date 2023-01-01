package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class BSOString implements BSOElement {
    private final String value;

    public BSOString(String value) {
        Objects.requireNonNull(value, "Null string not allowed");
        this.value = value;
    }

    public static BSOString of(String value) {
        return new BSOString(value);
    }

    @Override
    public BSOType<BSOString> getType() {
        return BSOTypes.STRING;
    }

    public String getValue() {
      return value;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        output.writeUTF(this.value);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitString(this);
    }

    @Override
    public BSOString copy() {
        return this;
    }

    @Override
    public String toString() {
        return this.asString();
    }
}
