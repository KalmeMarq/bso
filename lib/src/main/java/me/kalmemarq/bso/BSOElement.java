package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;

import me.kalmemarq.bso.number.BSOByte;
import me.kalmemarq.bso.number.BSODouble;
import me.kalmemarq.bso.number.BSOFloat;
import me.kalmemarq.bso.number.BSOInt;
import me.kalmemarq.bso.number.BSOLong;
import me.kalmemarq.bso.number.BSOShort;
import me.kalmemarq.bso.writer.StringBSOWriter;

public interface BSOElement {
    byte NULL_TYPE_ID = 0x00;
    byte BYTE_TYPE_ID = 0x01;
    byte SHORT_TYPE_ID = 0x02;
    byte INT_TYPE_ID = 0x03;
    byte LONG_TYPE_ID = 0x04;
    byte FLOAT_TYPE_ID = 0x05;
    byte DOUBLE_TYPE_ID = 0x06;
    byte STRING_TYPE_ID = 0x07;
    byte MAP_TYPE_ID = 0x08;
    byte LIST_TYPE_ID = 0x09;
    byte BYTE_ARRAY_TYPE_ID = 0x0A;
    byte SHORT_ARRAY_TYPE_ID = 0x0B;
    byte INT_ARRAY_TYPE_ID = 0x0C;
    byte LONG_ARRAY_TYPE_ID = 0x0D;
    byte FLOAT_ARRAY_TYPE_ID = 0x0E;
    byte DOUBLE_ARRAY_TYPE_ID = 0x0F;
    byte END_TYPE_ID = 0x10;

    /**
     * Returns this BSO Element byte ID
     * @return BSO Element byte ID
     */
    default byte getTypeId() {
        return this.getType().getId();
    }

    /**
     * Returns this element {@link BSOType}
     * @return Element {@link BSOType}
     */
    BSOType<?> getType();

    /**
     * Returns the element byte id plus the additional data. (ID + Additional Data).
     */
    default byte getIDAD() {
        return (byte) (getTypeId() + getAdditionalData());
    }

    void write(DataOutput output) throws IOException;

    /**
     * Gets additional data that changes how the element is written and read.
     */
    default int getAdditionalData() {
        return 0;
    }

    void accept(Visitor visitor);

    /**
     * Returns a copy of the element.
     * @return Copy of the element
     */
    BSOElement copy();

    String toString();

    /**
     * Returns {@link BSOElement} as SBSO.
     * @return {@link BSOElement} as SBSO
     */
    default String asString() {
        return new StringBSOWriter().apply(this);
    }

    public interface Visitor {
        void visitNull(BSONull element);
        void visitByte(BSOByte element);
        void visitShort(BSOShort element);
        void visitInt(BSOInt element);
        void visitLong(BSOLong element);
        void visitFloat(BSOFloat element);
        void visitDouble(BSODouble element);
        void visitString(BSOString element);
        void visitMap(BSOMap element);
        void visitList(BSOList element);
        void visitByteArray(BSOByteArray element);
        void visitShortArray(BSOShortArray element);
        void visitIntArray(BSOIntArray element);
        void visitLongArray(BSOLongArray element);
        void visitFloatArray(BSOFloatArray element);
        void visitDoubleArray(BSODoubleArray element);
    }
}
