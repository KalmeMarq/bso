package me.kalmemarq.bso.writer;

import java.util.Map;

import com.google.common.base.Strings;
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

public class BSOJsonWriter implements BSOElement.Visitor {
    private final StringBuilder output = new StringBuilder();
    private WriteStyle style = WriteStyle.MINIFY;
    private int indent = 2;
    private int level;

    private BSOJsonWriter(int level, WriteStyle style, int indent) {
        this.level = level;
        this.indent = indent;
        this.style = style;
    }

    public BSOJsonWriter() {
    }

    public String apply(BSOElement element) {
        element.accept(this);
        String result = this.output.toString();
        this.output.delete(0, this.output.length());
        return result;
    }

    public BSOJsonWriter setWriteStyle(WriteStyle style) {
        this.style = style;
        return this;
    }

    public BSOJsonWriter setIndent(int indent) {
        this.indent = Math.max(indent, 0);
        return this;
    }

    @Override
    public void visitNull(BSONull element) {
        output.append("null");
    }

    @Override
    public void visitByte(BSOByte element) {
        output.append(element.numberValue());
    }

    @Override
    public void visitShort(BSOShort element) {
        output.append(element.numberValue());
    }

    @Override
    public void visitInt(BSOInt element) {
        output.append(element.numberValue());
    }

    @Override
    public void visitLong(BSOLong element) {
        output.append(element.numberValue());
    }

    @Override
    public void visitFloat(BSOFloat element) {
        output.append(element.numberValue());
    }

    @Override
    public void visitDouble(BSODouble element) {
        output.append(element.numberValue());
    }

    @Override
    public void visitString(BSOString element) {
        output.append('"').append(element.getValue()).append('"');
    }

    @Override
    public void visitMap(BSOMap element) {
        output.append('{');

        if (style == WriteStyle.BEAUTIFY) {
            output.append("\n");
        } else if (style == WriteStyle.SPACED_MINIFY) {
            output.append(' ');
        }

        int i = 0;
        for (Map.Entry<String, BSOElement> entry : element.entries()) {
            if (style == WriteStyle.BEAUTIFY) {
                output.append(Strings.repeat(" ", (this.level + 1) * this.indent));
            }

            output.append('"').append(entry.getKey()).append('"');

            if (style != WriteStyle.MINIFY) {
                output.append(": ");
            } else {
                output.append(":");
            }

            output.append(new BSOJsonWriter(this.level + 1, this.style, this.indent).apply(entry.getValue()));

            if (i + 1 < element.size()) {
                output.append(",");

                if (style == WriteStyle.SPACED_MINIFY) {
                    output.append(' ');
                } else if (style == WriteStyle.BEAUTIFY) {
                    output.append('\n');
                }
            }

            ++i;
        }

        if (style == WriteStyle.BEAUTIFY) {
            output.append('\n').append(Strings.repeat(" ", this.level * this.indent));
        } else if (style == WriteStyle.SPACED_MINIFY) {
            output.append(' ');
        }

        output.append('}');
    }

    @Override
    public void visitList(BSOList element) {
        output.append('[');
        output.append(']');
    }

    @Override
    public void visitByteArray(BSOByteArray element) {
        output.append("[");

        byte[] vls = element.getByteArray();
        for (int i = 0; i < vls.length; i++) {
            output.append(vls[i]);

            if (i + 1 < vls.length) {
                output.append(',');

                if (style != WriteStyle.MINIFY) {
                    output.append(' ');
                }
            }
        }

        output.append("]");
    }

    @Override
    public void visitShortArray(BSOShortArray element) {
        output.append("[");
        short[] vls = element.getShortArray();
        for (int i = 0; i < vls.length; i++) {
            output.append(vls[i]);

            if (i + 1 < vls.length) {
                output.append(',');

                if (style != WriteStyle.MINIFY) {
                    output.append(' ');
                }
            }
        }
        output.append("]");
    }

    @Override
    public void visitIntArray(BSOIntArray element) {
        output.append("[");
        int[] vls = element.getIntArray();
        for (int i = 0; i < vls.length; i++) {
            output.append(vls[i]);

            if (i + 1 < vls.length) {
                output.append(',');

                if (style != WriteStyle.MINIFY) {
                    output.append(' ');
                }
            }
        }
        output.append("]");
    }

    @Override
    public void visitLongArray(BSOLongArray element) {
        output.append("[");
        long[] vls = element.getLongArray();
        for (int i = 0; i < vls.length; i++) {
            output.append(vls[i]);

            if (i + 1 < vls.length) {
                output.append(',');

                if (style != WriteStyle.MINIFY) {
                    output.append(' ');
                }
            }
        }
        output.append("]");
    }

    @Override
    public void visitFloatArray(BSOFloatArray element) {
        output.append("[");
        float[] vls = element.getFloatArray();
        for (int i = 0; i < vls.length; i++) {
            output.append(vls[i]);

            if (i + 1 < vls.length) {
                output.append(',');

                if (style != WriteStyle.MINIFY) {
                    output.append(' ');
                }
            }
        }
        output.append("]");
    }

    @Override
    public void visitDoubleArray(BSODoubleArray element) {
        output.append("[");
        double[] vls = element.getDoubleArray();
        for (int i = 0; i < vls.length; i++) {
            output.append(vls[i]);

            if (i + 1 < vls.length) {
                output.append(',');

                if (style != WriteStyle.MINIFY) {
                    output.append(' ');
                }
            }
        }
        output.append("]");
    }
}
