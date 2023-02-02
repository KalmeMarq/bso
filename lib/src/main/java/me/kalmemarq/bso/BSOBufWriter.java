package me.kalmemarq.bso;

import static me.kalmemarq.bso.BSOElement.END_TYPE_ID;

import java.io.IOException;
import java.util.Map;

import me.kalmemarq.bso.number.BSOByte;
import me.kalmemarq.bso.number.BSODouble;
import me.kalmemarq.bso.number.BSOFloat;
import me.kalmemarq.bso.number.BSOInt;
import me.kalmemarq.bso.number.BSOLong;
import me.kalmemarq.bso.number.BSOShort;

// Probably gonna delete. it's useless
public interface BSOBufWriter {
    void writeBEByte(int value) throws IOException;
    void writeBEShort(int value) throws IOException;
    void writeBEInt(int value) throws IOException;
    void writeBELong(long value) throws IOException;
    void writeBEFloat(float value) throws IOException;
    void writeBEDouble(double value) throws IOException;
    void writeUTF(String str) throws IOException;

    default void writeBSO(BSOElement value) throws IOException {
        byte type = value.getTypeId();
        if (type == BSOElement.BYTE_TYPE_ID) {
            writeBSOByte((BSOByte) value);
        } else if (type == BSOElement.SHORT_TYPE_ID) {
            writeBSOShort((BSOShort) value);
        } else if (type == BSOElement.INT_TYPE_ID) {
            writeBSOInt((BSOInt) value);
        } else if (type == BSOElement.LONG_TYPE_ID) {
            writeBSOLong((BSOLong) value);
        } else if (type == BSOElement.FLOAT_TYPE_ID) {
            writeBSOFloat((BSOFloat) value);
        } else if (type == BSOElement.DOUBLE_TYPE_ID) {
            writeBSODouble((BSODouble) value);
        } else if (type == BSOElement.MAP_TYPE_ID) {
            writeBSOMap((BSOMap) value);
        } else if (type == BSOElement.LIST_TYPE_ID) {
            writeBSOList((BSOList) value);
        } else if (type == BSOElement.BYTE_ARRAY_TYPE_ID) {
            writeBSOByteArray((BSOByteArray) value);
        } else if (type == BSOElement.SHORT_ARRAY_TYPE_ID) {
            writeBSOShortArray((BSOShortArray) value);
        } else if (type == BSOElement.INT_ARRAY_TYPE_ID) {
            writeBSOIntArray((BSOIntArray) value);
        } else if (type == BSOElement.LONG_ARRAY_TYPE_ID) {
            writeBSOLongArray((BSOLongArray) value);
        } else if (type == BSOElement.FLOAT_ARRAY_TYPE_ID) {
            writeBSOFloatArray((BSOFloatArray) value);
        } else if (type == BSOElement.DOUBLE_ARRAY_TYPE_ID) {
            writeBSODoubleArray((BSODoubleArray) value);
        } else {
            throw new IllegalStateException("Unexpected value: " + value.getType());
        }
    }

    default void writeBSOByte(BSOByte value) throws IOException {
        writeBEByte(value.asByte());
    }

    default void writeBSOShort(BSOShort value) throws IOException {
        writeBEShort(value.asShort());
    }

    default void writeBSOInt(BSOInt value) throws IOException {
        int vl = value.asInt();

        if (vl <= Byte.MAX_VALUE) {
            writeBEByte((byte) (vl & 0xFF));
        } else if (vl <= Short.MAX_VALUE) {
            writeBEShort((short) (vl & 0xFFFF));
        } else {
            writeBEInt(vl);
        }
    }

    default void writeBSOLong(BSOLong value) throws IOException {
        long vl = value.asLong();

        if (vl <= Byte.MAX_VALUE) {
            writeBEByte((byte)(vl & 0xFFL));
        } else if (vl <= Short.MAX_VALUE) {
            writeBEShort((short) (vl & 0xFFFFL));
        } else if (vl <= Integer.MAX_VALUE) {
            writeBEInt((int)(vl & 0xFFFFFFFFL));
        } else {
            writeBELong(vl);
        }
    }

    default void writeBSOFloat(BSOFloat value) throws IOException {
        writeBEFloat(value.asFloat());
    }

    default void writeBSODouble(BSODouble value) throws IOException {
        writeBEDouble(value.asDouble());
    }

    default void writeBSOMap(BSOMap value) throws IOException {
        if (!value.indefiniteLength) {
            BSOUtil.writeLength(this, value.entries.size());
        }

        for (Map.Entry<String, BSOElement> entry : value.entries.entrySet()) {
            writeBEByte((entry.getValue().getTypeId() + entry.getValue().getAdditionalData()));
            writeUTF(entry.getKey());
            writeBSO(entry.getValue());
        }

        if (value.indefiniteLength) writeBEByte(END_TYPE_ID);
    }

    default void writeBSOList(BSOList value) throws IOException {
        value.type = value.values.isEmpty() ? BSOTypes.NULL.getId(): value.values.get(0).getTypeId();

        writeBEByte(value.type);
        if (!value.indefiniteLength) BSOUtil.writeLength(this, value.values.size());

        for (BSOElement el : value.values) {
            writeBSO(el);
        }

        if (value.indefiniteLength) writeBEByte(END_TYPE_ID);
    }

    default void writeBSOByteArray(BSOByteArray value) throws IOException {
        byte[] vls = value.getByteArray();
        if (!value.indefiniteLength) BSOUtil.writeLength(this, vls.length);
        for (int i = 0; i < vls.length; i++) {
            writeBEByte(vls[i]);
        }
        if (value.indefiniteLength) writeBEByte(END_TYPE_ID);
    }

    default void writeBSOShortArray(BSOShortArray value) throws IOException {
        short[] vls = value.getShortArray();
        if (!value.indefiniteLength) BSOUtil.writeLength(this, vls.length);
        for (int i = 0; i < vls.length; i++) {
            writeBEShort(vls[i]);
        }
        if (value.indefiniteLength) this.writeBEByte(END_TYPE_ID);
    }

    default void writeBSOIntArray(BSOIntArray value) throws IOException {
        int[] vls = value.getIntArray();
        if (!value.indefiniteLength) BSOUtil.writeLength(this, vls.length);
        for (int i = 0; i < vls.length; i++) {
            writeBEInt(vls[i]);
        }
        if (value.indefiniteLength) this.writeBEByte(END_TYPE_ID);
    }

    default void writeBSOLongArray(BSOLongArray value) throws IOException {
        long[] vls = value.getLongArray();
        if (!value.indefiniteLength) BSOUtil.writeLength(this, vls.length);
        for (int i = 0; i < vls.length; i++) {
            writeBELong(vls[i]);
        }
        if (value.indefiniteLength) this.writeBEByte(END_TYPE_ID);
    }

    default void writeBSOFloatArray(BSOFloatArray value) throws IOException {
        float[] vls = value.getFloatArray();
        if (!value.indefiniteLength) BSOUtil.writeLength(this, vls.length);
        for (int i = 0; i < vls.length; i++) {
            writeBEFloat(vls[i]);
        }
        if (value.indefiniteLength) this.writeBEByte(END_TYPE_ID);
    }

    default void writeBSODoubleArray(BSODoubleArray value) throws IOException {
        double[] vls = value.getDoubleArray();
        if (!value.indefiniteLength) BSOUtil.writeLength(this, vls.length);
        for (int i = 0; i < vls.length; i++) {
            writeBEDouble(vls[i]);
        }
        if (value.indefiniteLength) this.writeBEByte(END_TYPE_ID);
    }
}
