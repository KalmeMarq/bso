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
    static final byte NULL_TYPE_ID = 0x00;
    static final byte BYTE_TYPE_ID = 0x01;
    static final byte SHORT_TYPE_ID = 0x02;
    static final byte INT_TYPE_ID = 0x03;
    static final byte LONG_TYPE_ID = 0x04;
    static final byte FLOAT_TYPE_ID = 0x05;
    static final byte DOUBLE_TYPE_ID = 0x06;
    static final byte STRING_TYPE_ID = 0x07;
    static final byte MAP_TYPE_ID = 0x08;
    static final byte LIST_TYPE_ID = 0x09;
    static final byte BYTE_ARRAY_TYPE_ID = 0x0A;
    static final byte SHORT_ARRAY_TYPE_ID = 0x0B;
    static final byte INT_ARRAY_TYPE_ID = 0x0C;
    static final byte LONG_ARRAY_TYPE_ID = 0x0D;
    static final byte FLOAT_ARRAY_TYPE_ID = 0x0E;
    static final byte DOUBLE_ARRAY_TYPE_ID = 0x0F;
    static final byte END_TYPE_ID = 0x10;

    default public byte getTypeId() {
        return this.getType().getId();
    }

    public BSOType<?> getType();
    
    public void write(DataOutput output) throws IOException;

    default public int getAdditionalData() {
        return 0;
    }

    public void accept(Visitor visitor);

    public BSOElement copy();

    public String toString();

    default public String asString() {
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
