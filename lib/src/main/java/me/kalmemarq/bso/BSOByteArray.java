package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import me.kalmemarq.bso.number.BSOByte;

public final class BSOByteArray extends AbstractBSOList<BSOByte> {
    private byte[] values;

    public BSOByteArray(byte[] values) {
        this.values = values;
    }
    
    public static BSOByteArray of(byte[] values) {
        return new BSOByteArray(values);
    }

    public static BSOByteArray of(boolean[] values) {
        byte[] vls = new byte[values.length];
        for (int i = 0; i < vls.length; i++) {
            vls[i] = (byte) (values[i] ? 1 : 0);
        }

        return new BSOByteArray(vls);
    }

    public static BSOByteArray of(String value) {
        return new BSOByteArray(value.getBytes(StandardCharsets.UTF_8));
    }

    public static BSOByteArray of(List<Byte> values) {
        byte[] vls = new byte[values.size()];
        for (int i = 0; i < vls.length; i++) {
            Byte b = values.get(i);
            vls[i] = b == null ? (byte) 0 : b;
        }
        return new BSOByteArray(vls);
    }

    @Override
    public BSOType<BSOByteArray> getType() {
        return BSOTypes.BYTE_ARRAY;
    }
    
    @Override
    public byte getHeldTypeId() {
        return BSOTypes.BYTE.getId();
    }

    @Override
    public void write(DataOutput output) throws IOException {
        if (!indefiniteLength) BSOUtils.writeLength(output, this.values.length);
        output.write(this.values);
        if (indefiniteLength) output.writeByte(END_TYPE_ID);
    }

    public byte[] getByteArray() {
      return this.values;
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitByteArray(this);
    }

    @Override
    public BSOByteArray copy() {
        byte[] vls = new byte[this.values.length];
        System.arraycopy(this.values, 0, vls, 0, this.values.length);
        return BSOByteArray.of(vls);
    }

    @Override
    public int size() {
        return this.values.length;
    }

    @Override
    public BSOByte set(int index, BSOByte value) {
        byte b = this.values[index];
        this.values[index] = value.getValue();
        return BSOByte.of(b);
    }

    @Override
    public void add(int index, BSOByte element) {
        byte[] vls = new byte[this.values.length + 1];
        for (int i = 0, j = 0; i < vls.length; i++) {
            if (i != index) {
                vls[i] = this.values[j];
                ++j;
            } else {
                vls[j] = element.getValue();
            }
        }
        this.values = vls;
    }

    @Override
    public BSOByte remove(int index) {
        byte b = this.values[index];
        byte[] vls = new byte[this.values.length - 1];
        for (int i = 0, j = 0; i < this.values.length; i++) {
            vls[j] = vls[i];
            if (j != index) i++;
        }
        this.values = vls;
        return BSOByte.of(b);
    }

    @Override
    public BSOByte get(int index) {
        return BSOByte.of(this.values[index]);
    }

    @Override
    public void clear() {
        this.values = new byte[0];
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BSOByteArray)) return false;
        return Arrays.equals(((BSOByteArray)obj).values, this.values);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(this.values);
    }

    @Override
    public String toString() {
        return this.asString();
    }
}
