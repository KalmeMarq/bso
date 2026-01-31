package io.github.kalmemarq.bso;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class SBsoUtils {
    public static BsoNode read(Path path) throws IOException {
        return new SBsoReader().read(path);
    }

    public static BsoNode read(String input) throws IOException {
        return new SBsoReader().read(input);
    }

    public static void write(Path path, BsoNode node) throws IOException {
        write(path, node, SBsoWriteOptions.MINIFIED);
    }

    public static void write(Path path, BsoNode node, SBsoWriteOptions options) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            writer.write(stringify(node, options));
        }
    }

    public static String stringify(BsoNode node) {
        return stringify(node, SBsoWriteOptions.MINIFIED);
    }

    public static String stringify(BsoNode node, SBsoWriteOptions options) {
        StringBuilder b = new StringBuilder();
        stringify(b, node, 0, options);
        return b.toString();
    }

    private static void stringify(StringBuilder builder, BsoNode node, int level, SBsoWriteOptions options) {
        switch (node) {
            case BsoMissing _ -> throw new IllegalArgumentException("BsoMissing is not allowed");
            case BsoByte(byte value) -> builder.append(value).append("sb");
            case BsoUByte(byte value) -> builder.append(Integer.toUnsignedString(value & 0xFF)).append("ub");
            case BsoShort(short value) -> builder.append(value).append("ss");
            case BsoUShort(short value) -> builder.append(Integer.toUnsignedString(value & 0xFFFF)).append("us");
            case BsoInt(int value) -> builder.append(value);
            case BsoUInt(int value) -> builder.append(Integer.toUnsignedString(value)).append("u");
            case BsoLong(long value) -> builder.append(value).append("sl");
            case BsoULong(long value) -> builder.append(Long.toUnsignedString(value)).append("ul");
            case BsoFloat(float value) -> builder.append(value).append("f");
            case BsoDouble(double value) -> builder.append(value).append("d");
            case BsoBool(boolean value) -> builder.append(value ? "true" : "false");
            case BsoString(String value) -> builder.append('"').append(escapeString(value)).append('"');
            case BsoMap n -> {
                builder.append('{');
                if (n.isEmpty()) {
                    builder.append('}');
                    break;
                }

                if (options.indent() > 0) builder.append('\n');

                int i = 0;
                for (var entry : n.properties()) {
                    if (i != 0) if (options.indent() > 0) builder.append('\n'); else builder.append(',');

                    if (options.indent() > 0) {
                        builder.repeat(' ', (level + 1) * options.indent());
                    }

                    builder.append(escapeKey(entry.getKey()));
                    builder.append(':');
                    if (options.indent() > 0) builder.append(' ');

                    stringify(builder, entry.getValue(), level + 1, options);
                    ++i;
                }

                if (options.indent() > 0) builder.append('\n');
                builder.append('}');
            }
            case BsoList n -> {
                builder.append('[');
                if (n.isEmpty()) {
                    builder.append(']');
                    break;
                }

                int i = 0;
                for (var value : n) {
                    if (i != 0) builder.append(',');
                    stringify(builder, value, level + 1, options);
                    ++i;
                }
                builder.append(']');
            }
            case BsoByteArray(byte[] values) -> {
                builder.append("[B;");
                for (int i = 0; i < values.length; ++i) {
                    if (i != 0) {
                        builder.append(',');
                        if (options.indent() > 0) builder.append(' ');
                    }
                    builder.append(values[i]);
                }
                builder.append(']');
            }
            case BsoUByteArray(byte[] values) -> {
                builder.append("[UB;");
                for (int i = 0; i < values.length; ++i) {
                    if (i != 0) {
                        builder.append(',');
                        if (options.indent() > 0) builder.append(' ');
                    }
                    builder.append(Integer.toUnsignedString(values[i] & 0xFF));
                }
                builder.append(']');
            }
            case BsoShortArray(short[] values) -> {
                builder.append("[S;");
                for (int i = 0; i < values.length; ++i) {
                    if (i != 0) {
                        builder.append(',');
                        if (options.indent() > 0) builder.append(' ');
                    }
                    builder.append(values[i]);
                }
                builder.append(']');
            }
            case BsoUShortArray(short[] values) -> {
                builder.append("[US;");
                for (int i = 0; i < values.length; ++i) {
                    if (i != 0) {
                        builder.append(',');
                        if (options.indent() > 0) builder.append(' ');
                    }
                    builder.append(Integer.toUnsignedString(values[i] & 0xFFFF));
                }
                builder.append(']');
            }
            case BsoIntArray(int[] values) -> {
                builder.append("[I;");
                for (int i = 0; i < values.length; ++i) {
                    if (i != 0) {
                        builder.append(',');
                        if (options.indent() > 0) builder.append(' ');
                    }
                    builder.append(values[i]);
                }
                builder.append(']');
            }
            case BsoUIntArray(int[] values) -> {
                builder.append("[UI;");
                for (int i = 0; i < values.length; ++i) {
                    if (i != 0) {
                        builder.append(',');
                        if (options.indent() > 0) builder.append(' ');
                    }
                    builder.append(Integer.toUnsignedString(values[i]));
                }
                builder.append(']');
            }
            case BsoLongArray(long[] values) -> {
                builder.append("[L;");
                for (int i = 0; i < values.length; ++i) {
                    if (i != 0) {
                        builder.append(',');
                        if (options.indent() > 0) builder.append(' ');
                    }
                    builder.append(values[i]);
                }
                builder.append(']');
            }
            case BsoULongArray(long[] values) -> {
                builder.append("[L;");
                for (int i = 0; i < values.length; ++i) {
                    if (i != 0) {
                        builder.append(',');
                        if (options.indent() > 0) builder.append(' ');
                    }
                    builder.append(Long.toUnsignedString(values[i]));
                }
                builder.append(']');
            }
            case BsoFloatArray(float[] values) -> {
                builder.append("[F;");
                for (int i = 0; i < values.length; ++i) {
                    if (i != 0) {
                        builder.append(',');
                        if (options.indent() > 0) builder.append(' ');
                    }
                    builder.append(values[i]);
                }
                builder.append(']');
            }
            case BsoDoubleArray(double[] values) -> {
                builder.append("[D;");
                for (int i = 0; i < values.length; ++i) {
                    if (i != 0) {
                        builder.append(',');
                        if (options.indent() > 0) builder.append(' ');
                    }
                    builder.append(values[i]);
                }
                builder.append(']');
            }
        }
    }

    private static String escapeKey(String input) {
        boolean isValidNakedKey = false;
        for (int i = 0; i < input.length(); ++i) {
            if (!(isValidNakedKey = isNakedKeyChar(i, input.charAt(i)))) {
                break;
            }
        }

        if (isValidNakedKey) {
            return input;
        } else {
            return '"' + escapeString(input) + '"';
        }
    }

    private static boolean isNakedKeyChar(int index, char chr) {
        boolean res = (chr >= 'a' && chr <= 'z') || (chr >= 'A' && chr <= 'Z') || chr == '_' || chr == '$';
        if (index != 0 && !res) res = (chr >= '0' && chr <= '9') || chr == '.';
        return res;
    }

    private static String escapeString(String input) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            char chr = input.charAt(i);
            if (chr == '\n') {
                b.append("\\n");
            } else if (chr == '\t') {
                b.append("\\t");
            } else if (chr == '\r') {
                b.append("\\r");
            } else if (chr == '\f') {
                b.append("\\f");
            } else if (chr == '"') {
                b.append("\\\"");
            } else {
                b.append(chr);
            }
        }
        return b.toString();
    }

    private SBsoUtils() {
    }
}
