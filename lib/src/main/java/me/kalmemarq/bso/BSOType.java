package me.kalmemarq.bso;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public abstract class BSOType<T extends BSOElement> {
    private final byte id;
    private final String name;

    public BSOType(byte id, String name) {
        this.id = id;
        this.name = name;
        BSOTypes.TYPES.put(id, this);
    }

    public byte getId() {
      return this.id;
    }

    public String getName() {
      return name;
    }

    abstract public T read(DataInput input, int additionalData) throws IOException;

    abstract public void write(DataOutput output, T element) throws IOException;
}
