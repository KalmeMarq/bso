package io.github.kalmemarq.bso.custom;

import io.github.kalmemarq.bso.BsoCustom;
import io.github.kalmemarq.bso.BsoCustom.BsoCustomType;
import io.github.kalmemarq.bso.SBsoReader;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.UUID;

public record UUIDType() implements BsoCustomType<UUID> {
    public static final UUIDType INSTANCE = new UUIDType();

    @Override
    public Class<UUID> getClazz() {
        return UUID.class;
    }

    @Override
    public int getId() {
        return 100;
    }

    @Override
    public BsoCustom<UUID> read(DataInput in, int ad) throws IOException {
        return new BsoCustom<>(this, new UUID(in.readLong(), in.readLong()));
    }

    @Override
    public void write(DataOutput out, BsoCustom<UUID> node) throws IOException {
        out.writeLong(node.value().getMostSignificantBits());
        out.writeLong(node.value().getLeastSignificantBits());
    }

    @Override
    public String getName() {
        return "uuid";
    }

    @Override
    public BsoCustom<UUID> parse(SBsoReader reader) throws IOException {
        StringBuilder b = new StringBuilder();
        while (reader.isHexDigit() || reader.currChr() == '-') {
            b.append((char) reader.currChr());
            reader.read();
        }
        return new BsoCustom<>(this, UUID.fromString(b.toString()));
    }

    @Override
    public String write(BsoCustom<UUID> node) {
        return node.value().toString();
    }
}
