package io.github.kalmemarq.bso;

import io.github.kalmemarq.bso.BsoCustom.BsoCustomType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SBsoReader {
    private Reader reader;
    private int currChr;
    private int line;
    private int column;

    public BsoNode read(Path path) throws IOException {
        this.currChr = -1;
        this.reader = new InputStreamReader(Files.newInputStream(path));
        this.line = 1;
        this.column = 1;
        this.read();
        return this.readNode();
    }

    public BsoNode read(String content) throws IOException {
        this.currChr = -1;
        this.reader = BufferedReader.of(content);
        this.line = 1;
        this.column = 1;
        this.read();
        return this.readNode();
    }

    private BsoNode readNode() throws IOException {
        switch (this.currChr) {
            case '{' -> {
                return this.readMap();
            }
            case '[' -> {
                return this.readList();
            }
            case 't' -> {
                return this.readTrue();
            }
            case 'f' -> {
                return this.readFalse();
            }
            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-' -> {
                return this.readNumber();
            }
            case '"' -> {
                return this.readString();
            }
            case '(' -> {
                return this.readCustom();
            }
        }

        return null;
    }

    private BsoNode readCustom() throws IOException {
        this.read();
        this.skipWhitespace();
        if (this.readChar(')')) {
            return new BsoMap();
        }

        StringBuilder nb = new StringBuilder();
        while (this.currChr != ';' && this.currChr != -1) {
            nb.append((char) this.currChr);
            this.read();

            if (this.isWhitespace()) {
                this.skipWhitespace();
                if (this.readChar(')')) {
                    return new BsoMap();
                } else {
                    this.readExpected(';');
                }
            }
        }

        if (this.currChr == ';') {
            this.read();
        }

        this.skipWhitespace();

        BsoCustomType<?> customType = BsoUtils.customTypeByName.get(nb.toString());
        if (customType != null) {
            int beginLine = this.line;
            int beginColumn = this.column;

            BsoNode node;
            try {
                node = customType.parse(this);
            } catch (Exception e) {
                throw new SBsoParseException(e.getMessage(), beginLine, beginColumn);
            }
            this.skipWhitespace();
            this.readExpected(')');
            return node;
        }

        throw new SBsoParseException("Failed to parse custom type", this.line, this.column);
    }

    public int readNum(StringBuilder b) throws IOException {
        boolean minus = this.currChr == '-';
        if (minus) {
            b.append('-');
            this.read();
        }

        int radix = 10;

        if (this.currChr == '0') {
            this.read();
            if (this.currChr == 'b') {
                this.read();
                while (this.isBinaryDigit()) {
                    b.append((char) this.currChr);
                    this.read();
                }
                radix = 2;
            } else if (this.currChr == 'x') {
                this.read();
                while (this.isHexDigit()) {
                    b.append((char) this.currChr);
                    this.read();
                }
                radix = 16;
            } else {
                b.append('0');
                while (this.isDigit()) {
                    b.append((char) this.currChr);
                    this.read();
                }
            }
        } else {
            while (this.isDigit()) {
                b.append((char) this.currChr);
                this.read();
            }
        }

        boolean decimal = this.readChar('.');
        if (decimal) {
            if (radix != 10) {
                throw new SBsoParseException("Malformed floating-point literal", this.line, this.column);
            }
            b.append('.');
            while (this.isDigit()) {
                b.append((char) this.currChr);
                this.read();
            }
        }

        return (decimal ? 1 << 16 : 0) | radix;
    }

    public BsoNode readNumber() throws IOException {
        StringBuilder b = new StringBuilder();

        int res = this.readNum(b);
        int radix = res & 0xFFFF;
        boolean decimal = (res >> 16 & 0xF) != 0;

        try {
            if (this.isPossibleNumericTypeIndicator()) {
                if (this.currChr == 'l') {
                    this.read();
                    return new BsoLong(Long.parseLong(b.toString(), radix));
                } else if (this.currChr == 'i') {
                    this.read();
                    return new BsoInt(Integer.parseInt(b.toString(), radix));
                } else if (this.currChr == 'd') {
                    this.read();
                    return new BsoDouble(Double.parseDouble(b.toString()));
                } else if (this.currChr == 'f') {
                    this.read();
                    return new BsoFloat(Float.parseFloat(b.toString()));
                } else if (this.currChr == 'b') {
                    this.read();
                    return new BsoByte(Byte.parseByte(b.toString(), radix));
                } else if (this.currChr == 's') {
                    this.read();

                    if (this.currChr == 'b') {
                        return new BsoByte(Byte.parseByte(b.toString(), radix));
                    } else if (this.currChr == 's') {
                        return new BsoShort(Short.parseShort(b.toString(), radix));
                    } else if (this.currChr == 'l') {
                        return new BsoLong(Long.parseLong(b.toString(), radix));
                    } else {
                        return new BsoInt(Integer.parseInt(b.toString(), radix));
                    }
                } else if (this.currChr == 'u') {
                    this.read();
                    if (this.currChr == 'b') {
                        this.read();
                        return new BsoUByte((byte) Integer.parseUnsignedInt(b.toString(), radix));
                    } else if (this.currChr == 's') {
                        this.read();
                        return new BsoUShort((short) Integer.parseUnsignedInt(b.toString(), radix));
                    } else if (this.currChr == 'l') {
                        this.read();
                        return new BsoULong(Long.parseUnsignedLong(b.toString(), radix));
                    } else {
                        return new BsoUInt(Integer.parseUnsignedInt(b.toString(), radix));
                    }
                }
            }

            return decimal ? new BsoDouble(Double.parseDouble(b.toString())) : new BsoInt(Integer.parseInt(b.toString(), radix));
        } catch (NumberFormatException e) {
            throw new SBsoParseException(e.getMessage(), this.line, this.column);
        }
    }

    private BsoNode readMap() throws IOException {
        this.read();
        this.skipWhitespace();
        if (this.readChar('}')) {
            return new BsoMap();
        }

        Map<String, BsoNode> map = new HashMap<>();

        do {
            this.skipWhitespace();
            String key = this.readMapKey();

            this.skipWhitespace();
            this.readExpected(':');
            this.skipWhitespace();
            BsoNode value = this.readNode();
            map.put(key, value);

            this.skipWhitespaceNoNL();

            if (this.readChar('\n')) {
                this.skipWhitespace();
                if (this.currChr == '}') {
                    break;
                }
            } else if (!this.readChar(',')) {
                break;
            }
        } while (true);

        this.skipWhitespace();
        this.readExpected('}');
        return new BsoMap(map);
    }

    private String readMapKey() throws IOException {
        boolean quoted = this.readChar('"');
        StringBuilder b = new StringBuilder();
        if (quoted) {
            while (this.currChr != '"') {
                b.append((char) this.currChr);
                this.read();
            }
            this.readExpected('"');
        } else {
            while (this.isNoQuoteKey()) {
                b.append((char) this.currChr);
                this.read();
            }
        }
        return b.toString();
    }

    private BsoNode readString() throws IOException {
        this.read();
        StringBuilder b = new StringBuilder();

        if (this.currChr == '"') {
            this.read();

            if (this.currChr == '"') {
                this.read();
                // Multiline
                this.readChar('\r');
                this.readExpected('\n');

                List<String> lines = new ArrayList<>();

                while (this.currChr != -1) {
                    if (this.currChr == '"') {
                        this.read();

                        if (this.currChr == '"') {
                            this.read();

                            if (this.currChr == '"') {
                                this.read();
                                break;
                            } else {
                                b.append('"');
                                continue;
                            }
                        } else {
                            b.append('"');
                            continue;
                        }
                    }

                    if (this.currChr == '\n') {
                        ++this.line;
                        lines.add(b.toString());
                        b = new StringBuilder();
                        this.read();
                        continue;
                    }
                    b.append((char) this.currChr);
                    this.read();
                }
                lines.add(b.toString());

                int minIndent = Integer.MAX_VALUE;

                for (String line : lines) {
                    int i = 0;
                    while (i < line.length()) {
                        if (!Character.isWhitespace(line.charAt(i))) {
                            minIndent = i;
                            break;
                        }
                        ++i;
                    }

                    minIndent = Math.min(minIndent, i);
                }

                b = new StringBuilder();
                int i = 0;
                for (String line : lines) {
                    if (i != 0) b.append('\n');
                    if (!line.isBlank())
                        b.append(line.substring(minIndent));
                    ++i;
                }
                return new BsoString(b.toString());
            } else {
                return new BsoString("");
            }
        }

        while (this.currChr != '"') {
            if (this.currChr == '\\') {
                this.read();
                switch (this.currChr) {
                    case '\\' -> b.append('\\');
                    case 'n' -> b.append('\n');
                    case 't' -> b.append('\t');
                    case 'r' -> b.append('\r');
                    case 'f' -> b.append('\f');
                    case 'b' -> b.append('\b');
                    case 'u' -> {
                        char[] chars = new char[4];
                        for (int i = 0; i < 4; i++) {
                            this.read();
                            if (!this.isHexDigit()) {
                                throw new SBsoParseException("Expected hexadecimal digit for \\u", this.line, this.column);
                            }
                            chars[i] = (char) this.currChr;
                        }
                        b.append((char) Integer.parseInt(new String(chars), 16));
                    }
                    default -> {
                        b.append('\\');
                        if (this.currChr != -1) b.append((char) this.currChr);
                    }
                }
                this.read();
            } else {
                b.append((char) this.currChr);
                this.read();
            }
        }
        this.readExpected('"');
        return new BsoString(b.toString());
    }

    private BsoNode readList() throws IOException {
        this.read();
        this.skipWhitespace();
        if (this.readChar(']')) {
            return new BsoList();
        }

        boolean hasUnsignedMark = this.readChar('U');
        if (this.readChar('B')) {
            this.readExpected(';');
            this.skipWhitespace();
            if (this.readChar(']')) {
                return hasUnsignedMark ? new BsoUByteArray(new byte[0]) : new BsoByteArray(new byte[0]);
            }

            byte[] array = new byte[32];
            int i = 0;

            StringBuilder b = new StringBuilder();
            do {
                this.skipWhitespace();

                b.setLength(0);
                int res = this.readNum(b);
                int radix = res & 0xFFFF;

                if (i + 1 >= array.length)
                    array = Arrays.copyOf(array, array.length * 2);

                array[i++] = hasUnsignedMark ? (byte) Integer.parseUnsignedInt(b.toString(), radix) : Byte.parseByte(b.toString(), radix);

                this.skipWhitespaceNoNL();

                if (this.readChar('\n')) {
                    this.skipWhitespace();
                    if (this.currChr == ']') {
                        break;
                    }
                } else if (!this.readChar(',')) {
                    break;
                }
            } while (true);

            this.skipWhitespace();
            this.readExpected(']');
            return hasUnsignedMark ? new BsoUByteArray(Arrays.copyOf(array, i)) : new BsoByteArray(Arrays.copyOf(array, i));
        } else if (this.readChar('S')) {
            this.readExpected(';');
            this.skipWhitespace();
            if (this.readChar(']')) {
                return new BsoShortArray(new short[0]);
            }

            short[] array = new short[16];
            int i = 0;

            StringBuilder b = new StringBuilder();
            do {
                this.skipWhitespace();

                b.setLength(0);
                int res = this.readNum(b);
                int radix = res & 0xFFFF;

                if (i + 1 >= array.length)
                    array = Arrays.copyOf(array, array.length * 2);

                array[i++] = hasUnsignedMark ? (short) Integer.parseUnsignedInt(b.toString(), radix) : Short.parseShort(b.toString(), radix);

                this.skipWhitespaceNoNL();

                if (this.readChar('\n')) {
                    this.skipWhitespace();
                    if (this.currChr == ']') {
                        break;
                    }
                } else if (!this.readChar(',')) {
                    break;
                }
            } while (true);

            this.skipWhitespace();
            this.readExpected(']');
            return new BsoShortArray(Arrays.copyOf(array, i));
        } else if (this.readChar('I')) {
            this.readExpected(';');
            this.skipWhitespace();
            if (this.readChar(']')) {
                return new BsoIntArray(new int[0]);
            }

            int[] array = new int[8];
            int i = 0;

            StringBuilder b = new StringBuilder();
            do {
                this.skipWhitespace();

                b.setLength(0);
                int res = this.readNum(b);
                int radix = res & 0xFFFF;

                if (i + 1 >= array.length)
                    array = Arrays.copyOf(array, array.length * 2);

                array[i++] = hasUnsignedMark ? Integer.parseUnsignedInt(b.toString(), radix) : Integer.parseInt(b.toString(), radix);

                this.skipWhitespaceNoNL();

                if (this.readChar('\n')) {
                    this.skipWhitespace();
                    if (this.currChr == ']') {
                        break;
                    }
                } else if (!this.readChar(',')) {
                    break;
                }
            } while (true);

            this.skipWhitespace();
            this.readExpected(']');
            return new BsoIntArray(Arrays.copyOf(array, i));
        } else if (this.readChar('L')) {
            this.readExpected(';');
            this.skipWhitespace();
            if (this.readChar(']')) {
                return new BsoLongArray(new long[0]);
            }

            long[] array = new long[4];
            int i = 0;

            StringBuilder b = new StringBuilder();
            do {
                this.skipWhitespace();

                b.setLength(0);
                int res = this.readNum(b);
                int radix = res & 0xFFFF;

                if (i + 1 >= array.length)
                    array = Arrays.copyOf(array, array.length * 2);

                array[i++] = hasUnsignedMark ? Long.parseUnsignedLong(b.toString(), radix) : Long.parseLong(b.toString(), radix);

                this.skipWhitespaceNoNL();

                if (this.readChar('\n')) {
                    this.skipWhitespace();
                    if (this.currChr == ']') {
                        break;
                    }
                } else if (!this.readChar(',')) {
                    break;
                }
            } while (true);

            this.skipWhitespace();
            this.readExpected(']');
            return new BsoLongArray(Arrays.copyOf(array, i));
        } else if (this.readChar('F')) {
            this.readExpected(';');
            this.skipWhitespace();
            if (this.readChar(']')) {
                return new BsoFloatArray(new float[0]);
            }

            float[] array = new float[8];
            int i = 0;

            StringBuilder b = new StringBuilder();
            do {
                this.skipWhitespace();

                b.setLength(0);
                this.readNum(b);

                if (i + 1 >= array.length)
                    array = Arrays.copyOf(array, array.length * 2);

                array[i++] = Float.parseFloat(b.toString());

                this.skipWhitespaceNoNL();

                if (this.readChar('\n')) {
                    this.skipWhitespace();
                    if (this.currChr == ']') {
                        break;
                    }
                } else if (!this.readChar(',')) {
                    break;
                }
            } while (true);

            this.skipWhitespace();
            this.readExpected(']');
            return new BsoFloatArray(Arrays.copyOf(array, i));
        } else if (this.readChar('D')) {
            this.readExpected(';');
            this.skipWhitespace();
            if (this.readChar(']')) {
                return new BsoDoubleArray(new double[0]);
            }

            double[] array = new double[8];
            int i = 0;

            StringBuilder b = new StringBuilder();
            do {
                this.skipWhitespace();

                b.setLength(0);
                this.readNum(b);

                if (i + 1 >= array.length)
                    array = Arrays.copyOf(array, array.length * 2);

                array[i++] = Double.parseDouble(b.toString());

                this.skipWhitespaceNoNL();

                if (this.readChar('\n')) {
                    this.skipWhitespace();
                    if (this.currChr == ']') {
                        break;
                    }
                } else if (!this.readChar(',')) {
                    break;
                }
            } while (true);

            this.skipWhitespace();
            this.readExpected(']');
            return new BsoDoubleArray(Arrays.copyOf(array, i));
        }

        List<BsoNode> list = new ArrayList<>();

        do {
            this.skipWhitespace();
            BsoNode value = this.readNode();
            list.add(value);

            this.skipWhitespaceNoNL();

            if (this.readChar('\n')) {
                this.skipWhitespace();
                if (this.currChr == ']') {
                    break;
                }
            } else if (!this.readChar(',')) {
                break;
            }
        } while (true);

        this.skipWhitespace();
        this.readExpected(']');
        return new BsoList(list);
    }

    public BsoNode readFalse() throws IOException {
        this.read();
        this.readExpected('a');
        this.readExpected('l');
        this.readExpected('s');
        this.readExpected('e');
        return BsoBool.FALSE;
    }

    public BsoNode readTrue() throws IOException {
        this.read();
        this.readExpected('r');
        this.readExpected('u');
        this.readExpected('e');
        return BsoBool.TRUE;
    }

    public void read() throws IOException {
        if (this.currChr == '\n') {
            this.column = 1;
            ++this.line;
        } else {
            ++this.column;
        }
        this.currChr = this.reader.read();
    }

    public int currChr() {
        return this.currChr;
    }

    public boolean readChar(char chr) throws IOException {
        if (this.currChr == chr) {
            this.read();
            return true;
        }
        return false;
    }

    public void readExpected(char chr) throws IOException {
        if (this.currChr != chr) {
            throw new SBsoParseException("Expected " + chr + " found " + (char) this.currChr, this.line, this.column);
        }
        this.read();
    }

    public void skipWhitespace() throws IOException {
        while (this.isWhitespace()) {
            this.read();
        }
    }

    public void skipWhitespaceNoNL() throws IOException {
        while (this.isWhitespaceNoNL()) {
            this.read();
        }
    }

    public boolean isWhitespace() {
        return this.currChr == ' ' || this.currChr == '\t' || this.currChr == '\r' || this.currChr == '\n';
    }

    public boolean isWhitespaceNoNL() {
        return this.currChr == ' ' || this.currChr == '\t' || this.currChr == '\r';
    }

    public boolean isNoQuoteKey() {
        return (this.currChr >= 'a' && this.currChr <= 'z') || (this.currChr >= 'A' && this.currChr <= 'Z') || this.currChr == '_' || this.currChr == '.';
    }

    public boolean isBinaryDigit() {
        return this.currChr == '0' || this.currChr == '1';
    }

    public boolean isDigit() {
        return (this.currChr >= '0' && this.currChr <= '9');
    }

    public boolean isHexDigit() {
        return (this.currChr >= '0' && this.currChr <= '9') || (this.currChr >= 'a' && this.currChr <= 'z') || (this.currChr >= 'A' && this.currChr <= 'Z');
    }

    public boolean isPossibleNumericTypeIndicator() {
        return this.currChr == 'i' || this.currChr == 'f' || this.currChr == 'd' || this.currChr == 'l' || this.currChr == 's' || this.currChr == 'u' || this.currChr == 'b';
    }

    public static class SBsoParseException extends IOException {
        public SBsoParseException(String message, int line, int column) {
            super(message + " at " + line + ":" + column);
        }
    }
}

