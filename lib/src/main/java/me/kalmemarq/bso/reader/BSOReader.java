package me.kalmemarq.bso.reader;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import me.kalmemarq.bso.BSOElement;
import me.kalmemarq.bso.BSOMap;
import me.kalmemarq.bso.BSONull;
import me.kalmemarq.bso.BSOType;
import me.kalmemarq.bso.BSOTypes;

public class BSOReader {
    public BSOReader() {}

    public BSOMap readMap(InputStream inputStream) throws IOException {
        BSOElement el = this.read(inputStream);
        try {
            return (BSOMap) el;
        } catch (ClassCastException e) {
            throw new RuntimeException("Top level element must be BSOMap but found " + el.getType().getName());
        }
    }

    public BSOElement read(InputStream inputStream) throws IOException {
        final DataInputStream input = new DataInputStream(inputStream);
        byte b = input.readByte();
        if ((b & 0x0F) == 0) return BSONull.INSTANCE;
        BSOType<?> type = BSOTypes.byId((byte)(b & 0x0F));
        return type.read(input, (byte) (b & 0xF0));
    }

    public BSOMap readMap(DataInput input) throws IOException {
        BSOElement el = this.read(input);
        try {
            return (BSOMap) el;
        } catch (ClassCastException e) {
            throw new RuntimeException("Top level element must be BSOMap but found " + el.getType().getName());
        }
    }

    public BSOElement read(DataInput input) throws IOException {
        byte b = input.readByte();
        if ((b & 0x0F) == 0) return BSONull.INSTANCE;
        BSOType<?> type = BSOTypes.byId((byte)(b & 0x0F));
        return type.read(input, (byte) (b & 0xF0));
    }
}
