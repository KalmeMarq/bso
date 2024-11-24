package me.kalmemarq.bso;

public interface BsoVisitor {
    void visitByte(BsoByte element);
    void visitBoolean(BsoBoolean element);
    void visitShort(BsoShort element);
    void visitInt(BsoInt element);
    void visitLong(BsoLong element);
    void visitFloat(BsoFloat element);
    void visitDouble(BsoDouble element);
    void visitString(BsoString element);
    void visitMap(BsoMap element);
    void visitList(BsoList element);
    void visitByteArray(BsoByteArray element);
    void visitBooleanArray(BsoBooleanArray element);
    void visitShortArray(BsoShortArray element);
    void visitIntArray(BsoIntArray element);
    void visitLongArray(BsoLongArray element);
    void visitFloatArray(BsoFloatArray element);
    void visitDoubleArray(BsoDoubleArray element);
}
