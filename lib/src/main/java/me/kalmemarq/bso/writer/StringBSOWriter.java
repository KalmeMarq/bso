package me.kalmemarq.bso.writer;

import java.util.Map;

import me.kalmemarq.bso.BSOByteArray;
import me.kalmemarq.bso.BSODoubleArray;
import me.kalmemarq.bso.BSOElement;
import me.kalmemarq.bso.BSOFloatArray;
import me.kalmemarq.bso.BSOIntArray;
import me.kalmemarq.bso.BSOList;
import me.kalmemarq.bso.BSOLongArray;
import me.kalmemarq.bso.BSOMap;
import me.kalmemarq.bso.BSONull;
import me.kalmemarq.bso.BSOShortArray;
import me.kalmemarq.bso.BSOString;
import me.kalmemarq.bso.number.BSOByte;
import me.kalmemarq.bso.number.BSODouble;
import me.kalmemarq.bso.number.BSOFloat;
import me.kalmemarq.bso.number.BSOInt;
import me.kalmemarq.bso.number.BSOLong;
import me.kalmemarq.bso.number.BSOShort;

public class StringBSOWriter implements BSOElement.Visitor {
    private StringBuilder output = new StringBuilder();
    // private final WriteStyle style;
    // private final int indent;
    // private int indentLevel;

    public StringBSOWriter() {
        // this(WriteStyle.MINIFY);
    }

    // public BSOStringWriter(WriteStyle style) {
    //     this(style, 2);
    // }

    // public BSOStringWriter(WriteStyle style, int indent) {
    //     this.style = style;
    //     this.indent = indent;
    // }

    public String apply(BSOElement element) {
        element.accept(this);
        String result = this.output.toString();
        // this.indentLevel = 0;
        this.output.delete(0, this.output.length()); // Might need to check if delete() is better than a new allocation
        return result;
    }

    @Override
    public void visitNull(BSONull element) {
        output.append("NULL");
    }

    @Override
    public void visitByte(BSOByte element) {
        output.append(element.getValue()).append("b");
    }

    @Override
    public void visitShort(BSOShort element) {
        output.append(element.getValue()).append("s");
    }

    @Override
    public void visitInt(BSOInt element) {
        output.append(element.getValue());
    }

    @Override
    public void visitLong(BSOLong element) {
        output.append(element.getValue()).append("L");
    }
    
    @Override
    public void visitFloat(BSOFloat element) {
        output.append(element.getValue()).append("f");
    }

    @Override
    public void visitDouble(BSODouble element) {
        element.getValue();
        output.append("").append("d");
    }

    @Override
    public void visitString(BSOString element) {
        output.append('"').append(element.getValue()).append('"');
    }

    @Override
    public void visitMap(BSOMap element) {
        output.append("{");
        int i = 0;
        for (Map.Entry<String, BSOElement> entry : element.entries()) {
            if (i != 0) output.append(",");
            output.append(entry.getKey()).append(':').append(new StringBSOWriter().apply(entry.getValue()));
            ++i;
        }
        output.append("}");
    }

    @Override
    public void visitList(BSOList element) {
        output.append('[');
        output.append(']');
    }

    @Override
    public void visitByteArray(BSOByteArray element) {
        output.append("[B;");
        byte[] vls = element.getByteArray();
        for (int i = 0; i < vls.length; i++) {
            if (i != 0) output.append(",");
            output.append(vls[i]);
        }
        output.append("]");
    }

    @Override
    public void visitShortArray(BSOShortArray element) {
        output.append("[S;");
        short[] vls = element.getShortArray();
        for (int i = 0; i < vls.length; i++) {
            if (i != 0) output.append(",");
            output.append(vls[i]);
        }
        output.append("]");
    }

    @Override
    public void visitIntArray(BSOIntArray element) {
        output.append("[I;");
        int[] vls = element.getIntArray();
        for (int i = 0; i < vls.length; i++) {
            if (i != 0) output.append(",");
            output.append(vls[i]);
        }
        output.append("]");
    }

    @Override
    public void visitLongArray(BSOLongArray element) {
        output.append("[L;");
        long[] vls = element.getLongArray();
        for (int i = 0; i < vls.length; i++) {
            if (i != 0) output.append(",");
            output.append(vls[i]);
        }
        output.append("]");
    }

    @Override
    public void visitFloatArray(BSOFloatArray element) {
        output.append("[L;");
        float[] vls = element.getFloatArray();
        for (int i = 0; i < vls.length; i++) {
            if (i != 0) output.append(",");
            output.append(vls[i]);
        }
        output.append("]");
    }

    @Override
    public void visitDoubleArray(BSODoubleArray element) {
        output.append("[D;");
        double[] vls = element.getDoubleArray();
        for (int i = 0; i < vls.length; i++) {
            if (i != 0) output.append(",");
            output.append(vls[i]);
        }
        output.append("]");
    }

    // public static enum WriteStyle {
    //     MINIFY,
    //     BEUTIFY;
    // }
}
