package me.kalmemarq.bso.writer;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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

public class StringBSOWriter implements BSOElement.Visitor {
    private static final Pattern SIMPLE_NAME = Pattern.compile("[a-zA-Z0-9_+-.*]+");
    private final StringBuilder output = new StringBuilder();
    private WriteStyle style = WriteStyle.MINIFY;
    private int indent = 2;
    private int level;

    private StringBSOWriter(int level, WriteStyle style, int indent) {
        this.level = level;
        this.indent = indent;
        this.style = style;
    }

    public StringBSOWriter() {
    }

    public String apply(BSOElement element) {
        element.accept(this);
        String result = this.output.toString();
        this.output.delete(0, this.output.length()); // Might need to check if delete() is better than a new allocation
        return result;
    }

    public StringBSOWriter setWriteStyle(WriteStyle style) {
        this.style = style;
        return this;
    }

    public StringBSOWriter setIndent(int indent) {
        this.indent = Math.max(indent, 0);
        return this;
    }

    @Override
    public void visitNull(BSONull element) {
        output.append("NULL");
    }

    @Override
    public void visitByte(BSOByte element) {
        output.append(element.asByte()).append("b");
    }

    @Override
    public void visitShort(BSOShort element) {
        output.append(element.asShort()).append("s");
    }

    @Override
    public void visitInt(BSOInt element) {
        output.append(element.asInt());
    }

    @Override
    public void visitLong(BSOLong element) {
        output.append(element.asLong()).append("L");
    }
    
    @Override
    public void visitFloat(BSOFloat element) {
        output.append(element.asFloat()).append("f");
    }

    @Override
    public void visitDouble(BSODouble element) {
        output.append(element.asDouble()).append("d");
    }

    @Override
    public void visitString(BSOString element) {
        output.append(BSOString.escape(element.getValue()));
    }

    @Override
    public void visitMap(BSOMap element) {
        output.append("{");

        if (style == WriteStyle.BEAUTIFY) {
            output.append("\n");
        } else if (style == WriteStyle.SPACED_MINIFY) {
            output.append(' ');
        }

        int i = 0;
        for (Map.Entry<String, BSOElement> entry : element.entrySet()) {
            if (style == WriteStyle.BEAUTIFY) {
                output.append(Strings.repeat(" ", (this.level + 1) * this.indent));
            }

            output.append(SIMPLE_NAME.matcher(entry.getKey()).matches() ? entry.getKey() : BSOString.escape(entry.getKey()));

            if (style != WriteStyle.MINIFY) {
                output.append(": ");
            } else {
                output.append(":");
            }

            output.append(new StringBSOWriter(this.level + 1, this.style, this.indent).apply(entry.getValue()));

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

        output.append("}");
    }

    @Override
    public void visitList(BSOList element) {
        output.append('[');

        int i = 0;
        List<BSOElement> els = element.getValues();
        boolean isAllPrimitive = true;

        for (BSOElement el : els) {
            if (el instanceof BSOMap || el instanceof BSOList) {
                isAllPrimitive = false;
                break;
            }
        }

        if (!isAllPrimitive && style == WriteStyle.BEAUTIFY) {
            output.append('\n');
        }

        for (BSOElement el : els) {
            if (style == WriteStyle.BEAUTIFY && !isAllPrimitive) {
                output.append(Strings.repeat(" ", (this.level + 1) * this.indent));
            }

            output.append(new StringBSOWriter(this.level + 1, this.style, this.indent).apply(el));

            if (i + 1 < els.size()) {
                output.append(',');

                if (style == WriteStyle.SPACED_MINIFY) {
                    output.append(' ');
                } else if (style == WriteStyle.BEAUTIFY) {
                    output.append(isAllPrimitive ? ' ' : '\n');
                }
            }
            ++i;
        }

        
        if (style == WriteStyle.BEAUTIFY && !isAllPrimitive) {
            output.append('\n').append(Strings.repeat(" ", this.level * this.indent));
        }

        output.append(']');
    }

    @Override
    public void visitByteArray(BSOByteArray element) {
        output.append("[B;");
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
        output.append("[S;");
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
        output.append("[I;");
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
        output.append("[L;");
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
        output.append("[F;");
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
        output.append("[D;");
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
