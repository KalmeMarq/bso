package me.kalmemarq.bso.reader;

public class StringReader {
    protected String data;
    protected int cursor;
    protected int line = 1;
    protected int column = 1;

    public StringReader(final String data) {
        this.data = data;
    }

    public void setDataAndReset(String data) {
        this.data = data;
        this.reset();
    }

    public void reset() {
        this.cursor = 0;
        this.line = 1;
        this.column = 1;
    }

    /**
     * Returns the current cursor position.
     * @return The current cursor position
     */
    public int getCursor() {
        return this.cursor;
    }

    public void setCursor(int position) {
        if (position >= this.data.length()) {
            this.cursor = this.data.length() - 1;
            return;
        }

        int l = 1;
        int c = 1;

        int i = 0;
        while (this.cursor >= this.data.length() && i < position) {
            if (this.currentChar() == '\n') {
                ++l;
                c = 1;
                ++i;
                continue;
            }
            ++c;
            ++i;
        }

        this.line = l;
        this.column = c;

        this.cursor = position;
    }

    /**
     * Returns true if cursor isn't at the end of the string.
     * @return {@code true} if cursor isn't at the end of the string
     */
    public boolean canRead() {
        return this.cursor < this.data.length();
    }

    /**
     * Returns the char at the current cursor position.
     * @return Char the current cursor position
     * @throws IndexOutOfBoundsException If the index argument is negative or not less than the length of this string.
     */
    public char currentChar() {
        return data.charAt(this.cursor);
    }

    /**
     * Returns the char at the cursor position plus the specified offset.
     * @return Char at the cursor position plus the specified offset
     */
    public char peek(int offset) {
        return data.charAt(this.cursor + offset);
    }

    public void advance() {
        ++this.cursor;
        ++this.column;

        if (canRead() && this.currentChar() == '\n') {
            this.column = 1;
            ++this.line;
            advance();
        }
    }

    public boolean isAllowedInUnquotedString(char chr, boolean isBeginning) {
        if (isBeginning) {
            return chr >= 'A' && chr <= 'Z' || chr >= 'a' && chr <= 'z' || chr == '_' || chr == '+' || chr == '-' || chr == '.' || chr == '*';
        } else {
            return chr >= '0' && chr <= '9' || chr >= 'A' && chr <= 'Z' || chr >= 'a' && chr <= 'z' || chr == '_' || chr == '+' || chr == '-' || chr == '.' || chr == '*';
        }
    }

    public boolean isAllowedInNumber(char chr) {
        return chr >= '0' && chr <= '9' || chr == '.' || chr == '-';
    }

    /**
     * Skips all the whitespaces characters from the current cursor position until it finds non whitespaces.
     */
    public void skipWhitespace() {
        while (this.canRead() && Character.isWhitespace(this.currentChar())) {
            if (this.currentChar() == '\n') {
                this.column = 1;
                ++this.line;
            }

            advance();
        }
    }

    /**
     * Expects the specified char and throws an exception if the char at the current cursor position or if the contents can't be read anymore.
     * @param chr The expected char at the current cursor position
     */
    public void expect(char chr) {
        if (!canRead() || currentChar() != chr) {
            throw createErrorWithPos("Expected " + chr + " but found " + this.currentChar());
        }
        advance();
    }

    public String readQuotedString() {
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

    public String readUnquotedString() {
        int curStart = this.cursor;

        boolean begin = true;
        while (canRead() && isAllowedInUnquotedString(currentChar(), begin)) {
            advance();
            if (begin) begin = false;
        }

        return this.data.substring(curStart, this.cursor);
    }

    public String readString() {
        if (!canRead()) return "";

        char n = currentChar();

        if (n == '"' || n == '\'') {
            return readQuotedString();
        }

        return readUnquotedString();
    }

    
    private int readInt(String type) {
        int curStart = this.getCursor();

        while (this.canRead() && isAllowedInNumber(this.currentChar())) {
            this.advance();
        }

        String num = this.data.substring(curStart, this.getCursor());

        if (num.isEmpty()) {
            throw this.createErrorWithPos("Expect " + type + " number but found nothing :(");
        }

        try {
            return Integer.parseInt(num);
        } catch (NumberFormatException e) {
            throw this.createErrorWithPos("Invalid " + type + " number");
        }
    }
    
    public byte readByte() {
        return (byte)this.readInt("byte");
    }

    public short readShort() {
        return (short)this.readInt("short");
    }

    public int readInt() {
        return readInt("int");
    }

    public long readLong() {
        int curStart = this.getCursor();

        while (this.canRead() && isAllowedInNumber(this.currentChar())) {
            this.advance();
        }

        String num = this.data.substring(curStart, this.getCursor());

        if (num.isEmpty()) {
            throw this.createErrorWithPos("Expect long number but found nothing :(");
        }

        try {
            return Long.parseLong(num);
        } catch (NumberFormatException e) {
            throw this.createErrorWithPos("Invalid long number");
        }
    }

    public float readFloat() {
        int curStart = this.getCursor();

        while (this.canRead() && isAllowedInNumber(this.currentChar())) {
            this.advance();
        }

        String num = this.data.substring(curStart, this.getCursor());

        if (num.isEmpty()) {
            throw this.createErrorWithPos("Expect float number but found nothing :(");
        }

        try {
            return Float.parseFloat(num);
        } catch (NumberFormatException e) {
            throw this.createErrorWithPos("Invalid float number");
        }
    }

    public double readDouble() {
        int curStart = this.getCursor();

        while (this.canRead() && isAllowedInNumber(this.currentChar())) {
            this.advance();
        }

        String num = this.data.substring(curStart, this.getCursor());

        if (num.isEmpty()) {
            throw this.createErrorWithPos("Expect double number but found nothing :(");
        }

        try {
            return Double.parseDouble(num);
        } catch (NumberFormatException e) {
            throw this.createErrorWithPos("Invalid double number");
        }
    }

    public RuntimeException createErrorWithPos(String msg, Object ...args) {
        return new RuntimeException(String.format(msg, args) + String.format(" at %d:%d", line, column));
    }

    public RuntimeException createErrorWithShowStarting(String msg, int length) {
        return new RuntimeException(msg + data.substring(this.cursor, Math.min(this.cursor + 5, this.data.length())));
    }
}
