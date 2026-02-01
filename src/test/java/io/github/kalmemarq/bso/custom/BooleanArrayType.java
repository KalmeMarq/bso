package io.github.kalmemarq.bso.custom;

import io.github.kalmemarq.bso.BsoCustom;
import io.github.kalmemarq.bso.BsoCustom.BsoCustomType;
import io.github.kalmemarq.bso.SBsoReader;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public record BooleanArrayType() implements BsoCustomType<boolean[]> {
    public static final BsoCustomType<boolean[]> INSTANCE = new BooleanArrayType();

    @Override
    public Class<boolean[]> getClazz() {
        return boolean[].class;
    }

    @Override
    public int getId() {
        return 101;
    }

    @Override
    public BsoCustom<boolean[]> read(DataInput in, int ad) throws IOException {
        int len = in.readInt();
        boolean[] array = new boolean[len];

        for (int i = 0; i < len; ++i) {
            array[i] = in.readByte() != 0;
        }

        return new BsoCustom<>(this, array);
    }

    @Override
    public void write(DataOutput out, BsoCustom<boolean[]> node) throws IOException {
        boolean[] array = node.value();
        out.writeInt(array.length);

        for (boolean b : array) {
            out.writeByte(b ? 1 : 0);
        }
    }

    @Override
    public String getName() {
        return "B[]";
    }

    @Override
    public BsoCustom<boolean[]> parse(SBsoReader reader) throws IOException {
        boolean[] array = new boolean[16];
        int i = 0;

        do {
            reader.skipWhitespace();
            if (reader.currChr() == 't') {
                reader.read();
                reader.readExpected('r');
                reader.readExpected('u');
                reader.readExpected('e');

                array[i++] = true;
            } else {
                reader.readExpected('f');
                reader.readExpected('a');
                reader.readExpected('l');
                reader.readExpected('s');
                reader.readExpected('e');

                array[i++] = false;
            }

            reader.skipWhitespaceNoNL();

            if (reader.readChar('\n')) {
                reader.skipWhitespace();
                if (reader.currChr() == ')') {
                    break;
                }
            } else if (!reader.readChar(',')) {
                break;
            }
        } while (true);

        reader.skipWhitespace();
        return new BsoCustom<>(this, array.length == i ? array : Arrays.copyOf(array, i));
    }

    @Override
    public String write(BsoCustom<boolean[]> node) {
        StringBuilder b = new StringBuilder();
        boolean[] array = node.value();
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) b.append(',');
            b.append(array[i] ? "true" : "false");
        }
        return b.toString();
    }
}
