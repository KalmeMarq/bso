package me.kalmemarq.bso.reader;

import java.util.ArrayList;
import java.util.List;

import me.kalmemarq.bso.BSOByteArray;
import me.kalmemarq.bso.BSODoubleArray;
import me.kalmemarq.bso.BSOElement;
import me.kalmemarq.bso.BSOFloatArray;
import me.kalmemarq.bso.BSOIntArray;
import me.kalmemarq.bso.BSOList;
import me.kalmemarq.bso.BSOLongArray;
import me.kalmemarq.bso.BSOMap;
import me.kalmemarq.bso.BSOShortArray;
import me.kalmemarq.bso.BSOString;
import me.kalmemarq.bso.BSOType;
import me.kalmemarq.bso.number.BSOByte;
import me.kalmemarq.bso.number.BSODouble;
import me.kalmemarq.bso.number.BSOFloat;
import me.kalmemarq.bso.number.BSOInt;
import me.kalmemarq.bso.number.BSOLong;
import me.kalmemarq.bso.number.BSOShort;

public class StringBSOReader {
    private String data;
    private int cursor;
    private int line;
    private int column;

    public StringBSOReader() {
    }

    public BSOMap read(String sbso) {
        BSOElement el = this.readSBSO(sbso);
        try {
            return (BSOMap) el;
        } catch (ClassCastException e) {
            throw new RuntimeException("Top level element must be BSOMap but found " + el.getType().getName());
        }
    }

    public BSOElement readSBSO(String sbso) {
        data = sbso;
        cursor = 0;
        line = 0;
        column = 0;
        return this.readElement();
    }

    private RuntimeException createErrorWithPos(String msg, Object ...args) {
        return new RuntimeException(String.format(msg, args) + String.format(" at %d:%d", line, column));
    }

    private boolean canRead() {
        return this.cursor < this.data.length();
    }

    private char currentChar() {
        return data.charAt(this.cursor);
    }

    private char peek(int amount) {
        return data.charAt(this.cursor + amount);
    }

    public void advance() {
        ++this.cursor;
        ++this.column;

        if (canRead() && this.currentChar() == '\n') {
            this.column = 0;
            ++this.line;
            advance();
        }
    }

    private void skipWhitespace() {
        while (this.canRead() && Character.isWhitespace(this.currentChar())) {
            if (this.currentChar() == '\n') {
                this.column = 0;
                ++this.line;
            }

            advance();
        }
    }

    private void expect(char chr) {
        if (!canRead() || currentChar() != chr) {
            throw createErrorWithPos("Expected " + chr + " but found " + this.currentChar());
        }
        advance();
    }

    private boolean isAllowedInUnquotedStr(char chr, boolean isBeginning) {
        if (isBeginning) {
            return chr >= 'A' && chr <= 'Z' || chr >= 'a' && chr <= 'z' || chr == '_' || chr == '+' || chr == '-' || chr == '.' || chr == '*';
        } else {
            return chr >= '0' && chr <= '9' || chr >= 'A' && chr <= 'Z' || chr >= 'a' && chr <= 'z' || chr == '_' || chr == '+' || chr == '-' || chr == '.' || chr == '*';
        }
    }

    public boolean isNumber(char chr) {
        return chr >= '0' && chr <= '9' || chr == '.' || chr == '-';
    }

    private byte readByte() {
        return (byte)this.readInt("byte");
    }

    private short readShort() {
        return (short)this.readInt("short");
    }

    private BSOElement readNumber() {
        int curStart = this.cursor;

        while (canRead() && isNumber(this.currentChar())) {
            this.advance();
        }

        skipWhitespace();

        String num = this.data.substring(curStart, this.cursor);

        if (num.isEmpty()) {
            throw createErrorWithPos("Expect number but found nothing :(");
        }

        if (currentChar() == 'b') {
            try {
                advance();
                return BSOByte.of((byte)Integer.parseInt(num));
            } catch (NumberFormatException e) {
                throw createErrorWithPos("Invalid byte number");
            }
        } else if (currentChar() == 's') {
            try {
                advance();
                return BSOShort.of((short)Integer.parseInt(num));
            } catch (NumberFormatException e) {
                throw createErrorWithPos("Invalid short number");
            }
        } else if (currentChar() == 'L') {
            try {
                advance();
                return BSOLong.of(Long.parseLong(num));
            } catch (NumberFormatException e) {
                throw createErrorWithPos("Invalid long number");
            }
        } else if (currentChar() == 'f') {
            try {
                advance();
                return BSOFloat.of(Float.parseFloat(num));
            } catch (NumberFormatException e) {
                throw createErrorWithPos("Invalid float number");
            }
        } else if (currentChar() == 'd' || currentChar() == 'D') {
            try {
                advance();
                return BSODouble.of(Double.parseDouble(num));
            } catch (NumberFormatException e) {
                throw createErrorWithPos("Invalid double number");
            }
        } else {
            try {
                return BSOInt.of(Integer.parseInt(num));
            } catch (NumberFormatException e) {
                throw createErrorWithPos("Invalid int number");
            }
        }
    }

    private int readInt(String type) {
        int curStart = this.cursor;

        while (canRead() && isNumber(this.currentChar())) {
            this.advance();
        }

        String num = this.data.substring(curStart, this.cursor);

        if (num.isEmpty()) {
            throw createErrorWithPos("Expect " + type + " number but found nothing :(");
        }

        try {
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            throw createErrorWithPos("Invalid " + type + " number");
        }
    }

    private int readInt() {
        return readInt("int");
    }

    private long readLong() {
        int curStart = this.cursor;

        while (canRead() && isNumber(this.currentChar())) {
            this.advance();
        }

        String num = this.data.substring(curStart, this.cursor);

        if (num.isEmpty()) {
            throw createErrorWithPos("Expect long number but found nothing :(");
        }

        try {
            return Long.parseLong(num);
        } catch (NumberFormatException e) {
            throw createErrorWithPos("Invalid long number");
        }
    }

    private float readFloat() {
        int curStart = this.cursor;

        while (canRead() && isNumber(this.currentChar())) {
            this.advance();
        }

        String num = this.data.substring(curStart, this.cursor);

        if (num.isEmpty()) {
            throw createErrorWithPos("Expect float number but found nothing :(");
        }

        try {
            return Float.parseFloat(num);
        } catch (NumberFormatException e) {
            throw createErrorWithPos("Invalid float number");
        }
    }

    private double readDouble() {
        int curStart = this.cursor;

        while (canRead() && isNumber(this.currentChar())) {
            this.advance();
        }

        String num = this.data.substring(curStart, this.cursor);

        if (num.isEmpty()) {
            throw createErrorWithPos("Expect double number but found nothing :(");
        }

        try {
            return Double.parseDouble(num);
        } catch (NumberFormatException e) {
            throw createErrorWithPos("Invalid double number");
        }
    }

    private String readQString() {
        int curStart = this.cursor;

        char q = currentChar();
        advance();

        while (canRead() && currentChar() != q) {
            advance();
        }

        int curEnd = this.cursor;

        advance();

        return this.data.substring(curStart + 1, curEnd);
    }

    private String readUQString() {
        int curStart = this.cursor;

        boolean begin = true;
        while (canRead() && isAllowedInUnquotedStr(currentChar(), begin)) {
            advance();
            if (begin) begin = false;
        }

        return this.data.substring(curStart, this.cursor);
    }

    private String readTString() {
        if (!canRead()) return "";

        char n = currentChar();

        if (n == '"' || n == '\'') {
            return readQString();
        }

        return readUQString();
    }

    private BSOMap readMap() {
        BSOMap map = new BSOMap();

        expect('{');
        skipWhitespace();

        while (canRead() && currentChar() != '}') {
            String key = this.readTString();
            skipWhitespace();
            expect(':');
            skipWhitespace();
            map.put(key, this.readElement());
            skipWhitespace();

            if (canRead()) {
                if (currentChar() == ',') {
                    advance();
                    if (currentChar() == '}') {
                        throw createErrorWithPos("Trailing comma");
                    }
                } else if (currentChar() != '}') {
                    throw createErrorWithPos("Missing comma");
                }
            } else {
                throw createErrorWithPos("Map ended badly");
            }

            skipWhitespace();
        }

        expect('}');
        return map;
    }

    private BSOList readList() {
        List<BSOElement> list = new ArrayList<>();

        expect('[');
        skipWhitespace();

        BSOType<?> type = null;
        while (canRead() && currentChar() != ']') {
            BSOElement el = this.readElement();
            if (type == null) {
                type = el.getType();
            } else if (type != el.getType()) {
                throw createErrorWithPos("List can only contain a single type. Found element of type %s when list is of type %s", el.getType().getName(), type.getName());
            }

            list.add(el);

            skipWhitespace();

            if (canRead()) {
                if (currentChar() == ',') {
                    advance();
                    skipWhitespace();

                    if (currentChar() == ']') {
                        throw createErrorWithPos("Trailing comma");
                    }
                } else if (currentChar() != ']') {
                    throw createErrorWithPos("Missing comma");
                }
            } else {
                throw createErrorWithPos("List ended badly");
            }
        }

        expect(']');
        return new BSOList(list);
    }

    private BSOByteArray readByteArray() {
        List<Byte> list = new ArrayList<>();

        expect('[');
        expect('B');
        expect(';');
        skipWhitespace();

        while (canRead() && currentChar() != ']') {
            byte num = this.readByte();
            list.add(num);

            if (currentChar() == 'b') { // Skip if number had the indicator. It's not a problem
                advance();
            }

            skipWhitespace();

            if (canRead()) {
                if (currentChar() == ',') {
                    advance();
                    skipWhitespace();

                    if (currentChar() == ']') {
                        throw createErrorWithPos("Trailing comma");
                    }
                } else if (currentChar() != ']') {
                    throw createErrorWithPos("Missing comma");
                }
            } else {
                throw createErrorWithPos("ByteArray ended badly");
            }
        }

        expect(']');
        skipWhitespace();
        return BSOByteArray.of(list);
    }

    private BSOShortArray readShortArray() {
        List<Short> list = new ArrayList<>();

        expect('[');
        expect('S');
        expect(';');
        skipWhitespace();

        while (canRead() && currentChar() != ']') {
            short num = this.readShort();
            list.add(num);

            if (currentChar() == 's') { // Skip if number had the indicator. It's not a problem
                advance();
            }

            skipWhitespace();

            if (canRead()) {
                if (currentChar() == ',') {
                    advance();
                    skipWhitespace();

                    if (currentChar() == ']') {
                        throw createErrorWithPos("Trailing comma");
                    }
                } else if (currentChar() != ']') {
                    throw createErrorWithPos("Missing comma");
                }
            } else {
                throw createErrorWithPos("ShortArray ended badly");
            }
        }

        expect(']');
        skipWhitespace();
        return BSOShortArray.of(list);
    }

    private BSOIntArray readIntArray() {
        List<Integer> list = new ArrayList<>();

        expect('[');
        expect('I');
        expect(';');
        skipWhitespace();

        while (canRead() && currentChar() != ']') {
            int num = this.readInt();
            list.add(num);
            skipWhitespace();

            if (canRead()) {
                if (currentChar() == ',') {
                    advance();
                    skipWhitespace();

                    if (currentChar() == ']') {
                        throw createErrorWithPos("Trailing comma");
                    }
                } else if (currentChar() != ']') {
                    throw createErrorWithPos("Missing comma");
                }
            } else {
                throw createErrorWithPos("IntArray ended badly");
            }
        }

        expect(']');
        skipWhitespace();
        return BSOIntArray.of(list);
    }

    private BSOLongArray readLongArray() {
        List<Long> list = new ArrayList<>();

        expect('[');
        expect('L');
        expect(';');
        skipWhitespace();

        while (canRead() && currentChar() != ']') {
            long num = this.readLong();
            list.add(num);

            if (currentChar() == 'L') { // Skip if number had the indicator. It's not a problem
                advance();
            }

            skipWhitespace();

            if (canRead()) {
                if (currentChar() == ',') {
                    advance();
                    skipWhitespace();

                    if (currentChar() == ']') {
                        throw createErrorWithPos("Trailing comma");
                    }
                } else if (currentChar() != ']') {
                    throw createErrorWithPos("Missing comma");
                }
            } else {
                throw createErrorWithPos("LongArray ended badly");
            }
        }

        expect(']');
        skipWhitespace();
        return BSOLongArray.of(list);
    }

    private BSOFloatArray readFloatArray() {
        List<Float> list = new ArrayList<>();

        expect('[');
        expect('F');
        expect(';');
        skipWhitespace();

        while (canRead() && currentChar() != ']') {
            float num = this.readFloat();
            list.add(num);

            if (currentChar() == 'f') { // Skip if number had the indicator. It's not a problem
                advance();
            }

            skipWhitespace();

            if (canRead()) {
                if (currentChar() == ',') {
                    advance();
                    skipWhitespace();

                    if (currentChar() == ']') {
                        throw createErrorWithPos("Trailing comma");
                    }
                } else if (currentChar() != ']') {
                    throw createErrorWithPos("Missing comma");
                }
            } else {
                throw createErrorWithPos("FloatArray ended badly");
            }
        }

        expect(']');
        skipWhitespace();
        return BSOFloatArray.of(list);
    }

    private BSODoubleArray readDoubleArray() {
        List<Double> list = new ArrayList<>();

        expect('[');
        expect('D');
        expect(';');
        skipWhitespace();

        while (canRead() && currentChar() != ']') {
            double num = this.readDouble();
            list.add(num);

            if (currentChar() == 'd' || currentChar() == 'D') { // Skip if number had the indicator. It's not a problem
                advance();
            }

            skipWhitespace();

            if (canRead()) {
                if (currentChar() == ',') {
                    advance();
                    skipWhitespace();

                    if (currentChar() == ']') {
                        throw createErrorWithPos("Trailing comma");
                    }
                } else if (currentChar() != ']') {
                    throw createErrorWithPos("Missing comma");
                }
            } else {
                throw createErrorWithPos("DoubleArray ended badly");
            }
        }

        expect(']');
        skipWhitespace();
        return BSODoubleArray.of(list);
    }

    private BSOElement readElement() {
        skipWhitespace();

        if (this.currentChar() == '{') {
            return this.readMap();
        } else if (this.currentChar() == '[' && this.peek(1) == 'B') {
            return this.readByteArray();
        } else if (this.currentChar() == '[' && this.peek(1) == 'S') {
            return this.readShortArray();
        } else if (this.currentChar() == '[' && this.peek(1) == 'I') {
            return this.readIntArray();
        } else if (this.currentChar() == '[' && this.peek(1) == 'L') {
            return this.readLongArray();
        } else if (this.currentChar() == '[' && this.peek(1) == 'F') {
            return this.readFloatArray();
        } else if (this.currentChar() == '[' && this.peek(1) == 'D') {
            return this.readDoubleArray();
        } else if (this.currentChar() == '[') {
            return this.readList();
        } else if (this.currentChar() == '"' || this.currentChar() == '\'') {
            return BSOString.of(this.readQString());
        } else if (isNumber(this.currentChar())) {
            return this.readNumber();
        } else {
            throw new RuntimeException("Unknown type started with " + data.substring(this.cursor, Math.min(this.cursor + 5, this.data.length())));
        }
    }
}
