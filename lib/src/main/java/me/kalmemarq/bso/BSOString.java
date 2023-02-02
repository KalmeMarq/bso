package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

public class BSOString implements BSOElement, Comparable<BSOString> {
    public static final int LENGTH_LIMIT = 65535;
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
        if (this.value.length() > LENGTH_LIMIT) BSOUtil.writeIndefiniteUTF8(output, this.value);
        else output.writeUTF(this.value);
    }

    @Override
    public int getAdditionalData() {
        return this.value.length() > LENGTH_LIMIT ? 0x10 : 0x00;
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

    @Override
    public int compareTo(BSOString obj) {
        return this.value.compareTo(obj.value);
    }

    private static final int DOUBLE_QUOTE = 34;
    private static final int SINGLE_QUOTE = 39;
    private static final int BLACKSLASH = 92;

    public static String escape(String str) {
        StringBuilder builder = new StringBuilder(" ");
        int quote = 0;

        for (int i = 0; i < str.length(); i++) {
            int c = str.charAt(i);

            if (c == BLACKSLASH) {
                builder.append('\\');
            } else if (c == DOUBLE_QUOTE || c == SINGLE_QUOTE) {
                if (quote == 0) {
                    quote = c == DOUBLE_QUOTE ? SINGLE_QUOTE : DOUBLE_QUOTE;
                }

                if (quote == c) {
                    builder.append('\\');
                }
            }

            builder.append((char)c);
        }

        if (quote == 0) {
            quote = DOUBLE_QUOTE;
        }

        builder.setCharAt(0, (char)quote);
        builder.append((char)quote);
        return builder.toString();
    }
}
