package me.kalmemarq.bso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SBsoReader {
    private static final Pattern DOUBLE_PATTERN_IMPLICIT = Pattern.compile("[-+]?(?:[0-9]+[.]|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?", 2);
    private static final Pattern DOUBLE_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?d", Pattern.CASE_INSENSITIVE);
    private static final Pattern FLOAT_PATTERN = Pattern.compile("[-+]?(?:[0-9]+[.]?|[0-9]*[.][0-9]+)(?:e[-+]?[0-9]+)?f", Pattern.CASE_INSENSITIVE);
    private static final Pattern BYTE_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)b", Pattern.CASE_INSENSITIVE);
    private static final Pattern LONG_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)l", Pattern.CASE_INSENSITIVE);
    private static final Pattern SHORT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)s", Pattern.CASE_INSENSITIVE);
    private static final Pattern INT_PATTERN = Pattern.compile("[-+]?(?:0|[1-9][0-9]*)");
    private int cursor;
    private String content;

    public SBsoReader() {
    }

    public BsoElement read(String content) {
        this.cursor = 0;
        this.content = content;
        return this.readElement();
    }

    @SuppressWarnings("unchecked")
    public <T extends BsoElement> T read(String content, Class<T> clazz) {
        BsoElement element = this.read(content);
        return element != null && element.getClass() == clazz ? (T) element : null;
    }

    private BsoElement readElement() {
        this.skipWhitespace();
        if (!this.canRead()) {
            return null;
        }
        char c = this.peek();
        if (c == '{') {
            return this.readMap();
        }
        if (c == '[') {
            return this.readList();
        }
        if (c == '\'') {
            return new BsoString(this.readQuotedString());
        }
        String s = this.readUnquotedString();
        if (!s.isEmpty()) {
            if (FLOAT_PATTERN.matcher(s).matches()) {
                return new BsoFloat((Float.parseFloat(s.substring(0, s.length() - 1))));
            }
            if (BYTE_PATTERN.matcher(s).matches()) {
                return new BsoByte(Byte.parseByte(s.substring(0, s.length() - 1)));
            }
            if (LONG_PATTERN.matcher(s).matches()) {
                return new BsoLong(Long.parseLong(s.substring(0, s.length() - 1)));
            }
            if (SHORT_PATTERN.matcher(s).matches()) {
                return new BsoShort(Short.parseShort(s.substring(0, s.length() - 1)));
            }
            if (INT_PATTERN.matcher(s).matches()) {
                return new BsoInt(Integer.parseInt(s));
            }
            if (DOUBLE_PATTERN.matcher(s).matches()) {
                return new BsoDouble(Double.parseDouble(s.substring(0, s.length() - 1)));
            }
            if (DOUBLE_PATTERN_IMPLICIT.matcher(s).matches()) {
                return new BsoDouble(Double.parseDouble(s));
            }
            if ("true".equalsIgnoreCase(s)) {
                return BsoBoolean.TRUE;
            }
            if ("false".equalsIgnoreCase(s)) {
                return BsoBoolean.FALSE;
            }
        }
        return null;
    }

    private BsoMap readMap() {
        BsoMap map = new BsoMap();
        this.skip();
        this.skipWhitespace();
        while (this.canRead() && this.peek() != '}') {
            String key = this.peek() == '\'' ? this.readQuotedString() : this.readUnquotedString();
            this.skipWhitespace();
            this.skip();
            this.skipWhitespace();
            BsoElement element = this.readElement();
            if (element == null) {
                return null;
            }
            map.put(key, element);
            this.skipWhitespace();
            if (!this.readComma()) break;
        }
        this.skip();
        return map;
    }

    @SuppressWarnings("unchecked")
    private BsoElement readList() {
        if (this.canRead(3) && this.peek(1) != '\'' && this.peek(2) == ';') {
            this.skip();
            char chr = this.peek();
            this.skip();
            this.skip();
            this.skipWhitespace();
            if (chr == 'B' || chr == 'b') {
                List<Byte> l = this.readArray('B', BsoByte.class, BsoInt.class);
                if (l == null) return null;
                this.skipWhitespace();
                this.skip();
                return new BsoByteArray(l);
            } else if (chr == 'Z' || chr == 'z') {
                List<Byte> l = this.readArray('Z', BsoBoolean.class);
                if (l == null) return null;
                this.skipWhitespace();
                this.skip();
                return new BsoBooleanArray(l);
            } else if (chr == 'S' || chr == 's') {
                List<Short> l = this.readArray('S', BsoShort.class, BsoInt.class);
                if (l == null) return null;
                this.skipWhitespace();
                this.skip();
                return new BsoShortArray(l);
            } else if (chr == 'I' || chr == 'i') {
                List<Integer> l = this.readArray('I', BsoInt.class);
                if (l == null) return null;
                this.skipWhitespace();
                this.skip();
                return new BsoIntArray(l);
            } else if (chr == 'L' || chr == 'l') {
                List<Long> l = this.readArray('L', BsoInt.class);
                if (l == null) return null;
                this.skipWhitespace();
                this.skip();
                return new BsoLongArray(l);
            } else if (chr == 'F' || chr == 'f') {
                List<Float> l = this.readArray('F', BsoInt.class);
                if (l == null) return null;
                this.skipWhitespace();
                this.skip();
                return new BsoFloatArray(l);
            } else if (chr == 'D' || chr == 'd') {
                List<Double> l = this.readArray('D', BsoInt.class);
                if (l == null) return null;
                this.skipWhitespace();
                this.skip();
                return new BsoDoubleArray(l);
            }
            return null;
        } else {
            BsoList list = new BsoList();
            this.skip();
            this.skipWhitespace();
            while (this.canRead() && this.peek() != ']') {
                BsoElement element = this.readElement();
                if (element == null) return null;
                list.add(element);
                if (!this.readComma()) break;
            }
            this.skipWhitespace();
            this.skip();
            return list;
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Number> List<T> readArray(char type, Class<? extends BsoNumeric>... clazz) {
        List<Number> list = new ArrayList<>();
        while (this.canRead() && this.peek() != ']') {
            BsoElement element = this.readElement();
            if (element == null || (element.getClass() != clazz[0] && (clazz.length != 2 || element.getClass() != clazz[1]))) {
                return null;
            }
            if (type == 'B' || type == 'Z') {
                list.add(((BsoNumeric) element).byteValue());
            } else if (type == 'S') {
                list.add(((BsoNumeric) element).shortValue());
            } else if (type == 'I') {
                list.add(((BsoNumeric) element).intValue());
            } else if (type == 'L') {
                list.add(((BsoNumeric) element).longValue());
            } else if (type == 'F') {
                list.add(((BsoNumeric) element).floatValue());
            } else if (type == 'D') {
                list.add(((BsoNumeric) element).doubleValue());
            }
            if (!this.readComma()) break;
        }
        return (List<T>) list;
    }

    private boolean canRead(int length) {
        return this.cursor + length <= this.content.length() && this.cursor + length >= 0;
    }

    private boolean canRead() {
        return this.canRead(1);
    }

    private char peek(int offset) {
        return this.content.charAt(this.cursor + offset);
    }

    private char peek() {
        return this.content.charAt(this.cursor);
    }

    private void skip() {
        this.cursor++;
    }

    private void skipWhitespace() {
        while (this.canRead() && Character.isWhitespace(this.peek())) {
            this.skip();
        }
    }

    private static boolean isAllowedInUnquotedString(final char c) {
        return c >= '0' && c <= '9'
                || c >= 'A' && c <= 'Z'
                || c >= 'a' && c <= 'z'
                || c == '_' || c == '-'
                || c == '.' || c == '+';
    }

    private boolean readComma() {
        this.skipWhitespace();
        if (this.canRead() && this.peek() == ',') {
            this.skip();
            this.skipWhitespace();
            return true;
        }
        return false;
    }

    private String readUnquotedString() {
        int start = this.cursor;
        while (this.canRead() && isAllowedInUnquotedString(this.peek())) {
            this.skip();
        }
        return this.content.substring(start, this.cursor);
    }

    private String readQuotedString() {
        if (!this.canRead()) {
            return "";
        }
        char next = this.peek();
        if (next != '\'') {
            return null;
        }
        this.skip();

        StringBuilder builder = new StringBuilder();

        while (this.canRead()) {
            if (this.peek(-1) != '\\' && this.peek() == '`') {
                this.skip();
                break;
            }

            if (this.peek() == '\\' && this.canRead(1)) {
                if (this.peek(1) == '`') {
                    builder.append('`');
                    this.skip();
                    this.skip();
                    continue;
                }
            }

            builder.append(this.peek());
            this.skip();
        }
        return builder.toString();
    }
}
