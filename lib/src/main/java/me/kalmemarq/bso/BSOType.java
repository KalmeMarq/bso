package me.kalmemarq.bso;

import java.io.DataInput;
import java.io.IOException;

public abstract class BSOType<T extends BSOElement> {
    private final byte id;

    public BSOType(byte id) {
        this.id = id;
        BSOTypes.TYPES.put(id, this);
    }

    public byte getId() {
      return this.id;
    }

    abstract public T read(DataInput input, int additionalData) throws IOException;
}
