package me.kalmemarq.bso;

import java.util.regex.Pattern;

public class SBsoWriter implements BsoVisitor {
    private static final Pattern SIMPLE_NAME = Pattern.compile("[A-Za-z0-9._+-]+");
    private StringBuilder builder;
    private int indent = -1;
    private int level = 0;

    public SBsoWriter() {
    }

    public String apply(BsoElement element) {
        return this.apply(element, -1);
    }

    public String apply(BsoElement element, int indent) {
        this.indent = indent;
        this.level = 0;
        this.builder = new StringBuilder();
        element.visit(this);
        return this.builder.toString();
    }

    @Override
    public void visitByte(BsoByte element) {
        this.builder.append(element.byteValue()).append('b');
    }

    @Override
    public void visitBoolean(BsoBoolean element) {
        this.builder.append(element.booleanValue() ? "true" : "false");
    }

    @Override
    public void visitShort(BsoShort element) {
        this.builder.append(element.shortValue()).append('s');
    }

    @Override
    public void visitInt(BsoInt element) {
        this.builder.append(element.intValue());
    }

    @Override
    public void visitLong(BsoLong element) {
        this.builder.append(element.longValue()).append('L');
    }

    @Override
    public void visitFloat(BsoFloat element) {
        this.builder.append(element.floatValue()).append('f');
    }

    @Override
    public void visitDouble(BsoDouble element) {
        this.builder.append(element.doubleValue()).append('D');
    }

    @Override
    public void visitString(BsoString element) {
        this.builder.append('\'').append(BsoUtils.escapeNameForQuote(element.value())).append('`');
    }

    @Override
    public void visitMap(BsoMap element) {
        this.builder.append('{');

        if (this.indent > 0 && !element.isEmpty()) {
            this.builder.append('\n');
        }

        int i = 0;
        for (String key : element.keySet()) {
            if (i != 0) this.builder.append(this.indent > -1 ? ", " : ",");
            if (this.indent > 0) {
                if (i != 0) this.builder.append('\n');
                BsoUtils.appendRepeat(this.builder, ' ', this.indent * (this.level + 1));
            }
            if (SIMPLE_NAME.matcher(key).matches()) {
                this.builder.append(key);
            } else {
                this.builder.append('\'').append(BsoUtils.escapeNameForQuote(key)).append('`');
            }
            this.builder.append(':');
            if (this.indent > -1) this.builder.append(' ');
            int level = this.level;
            this.level++;
            element.get(key).visit(this);
            this.level = level;
            ++i;
        }

        if (this.indent > 0 && !element.isEmpty()) {
            this.builder.append('\n');
            BsoUtils.appendRepeat(this.builder, ' ', this.indent * this.level);
        }

        this.builder.append('}');
    }

    @Override
    public void visitList(BsoList element) {
        this.builder.append('[');

        if (this.indent > 0 && !element.isEmpty()) {
            this.builder.append('\n');
        }

        int i = 0;
        for (BsoElement value : element) {
            if (i != 0) this.builder.append(this.indent > -1 ? ", " : ",");
            if (this.indent > 0) {
                if (i != 0) this.builder.append('\n');
                BsoUtils.appendRepeat(this.builder, ' ', this.indent * (this.level + 1));
            }
            int level = this.level;
            this.level++;
            value.visit(this);
            this.level = level;
            ++i;
        }

        if (this.indent > 0 && !element.isEmpty()) {
            this.builder.append('\n');
            BsoUtils.appendRepeat(this.builder, ' ', this.indent * this.level);
        }

        this.builder.append(']');
    }

    @Override
    public void visitByteArray(BsoByteArray element) {
        this.builder.append("[B;");
        int i = 0;
        for (byte value : element.array()) {
            if (i != 0) this.builder.append(this.indent > -1 ? ", " : ",");
            this.builder.append(value);
            ++i;
        }
        this.builder.append(']');
    }

    @Override
    public void visitBooleanArray(BsoBooleanArray element) {
        this.builder.append("[Z;");
        int i = 0;
        for (boolean value : element.array()) {
            if (i != 0) this.builder.append(this.indent > -1 ? ", " : ",");
            this.builder.append(value);
            ++i;
        }
        this.builder.append(']');
    }

    @Override
    public void visitShortArray(BsoShortArray element) {
        this.builder.append("[S;");
        int i = 0;
        for (short value : element.array()) {
            if (i != 0) this.builder.append(this.indent > -1 ? ", " : ",");
            this.builder.append(value);
            ++i;
        }
        this.builder.append(']');
    }

    @Override
    public void visitIntArray(BsoIntArray element) {
        this.builder.append("[I;");
        int i = 0;
        for (int value : element.array()) {
            if (i != 0) this.builder.append(this.indent > -1 ? ", " : ",");
            this.builder.append(value);
            ++i;
        }
        this.builder.append(']');
    }

    @Override
    public void visitLongArray(BsoLongArray element) {
        this.builder.append("[L;");
        int i = 0;
        for (long value : element.array()) {
            if (i != 0) this.builder.append(this.indent > -1 ? ", " : ",");
            this.builder.append(value);
            ++i;
        }
        this.builder.append(']');
    }

    @Override
    public void visitFloatArray(BsoFloatArray element) {
        this.builder.append("[F;");
        int i = 0;
        for (float value : element.array()) {
            if (i != 0) this.builder.append(this.indent > -1 ? ", " : ",");
            this.builder.append(value);
            ++i;
        }
        this.builder.append(']');
    }

    @Override
    public void visitDoubleArray(BsoDoubleArray element) {
        this.builder.append("[D;");
        int i = 0;
        for (double value : element.array()) {
            if (i != 0) this.builder.append(this.indent > -1 ? ", " : ",");
            this.builder.append(value);
            ++i;
        }
        this.builder.append(']');
    }
}
