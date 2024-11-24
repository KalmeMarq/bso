package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;

public interface BsoElement {
    int getId();
    int getAdditionalData();
    void write(DataOutput output) throws IOException;
    void visit(BsoVisitor visitor);
    BsoElement copy();

    default String asString() {
        return new SBsoWriter().apply(this);
    }
}
