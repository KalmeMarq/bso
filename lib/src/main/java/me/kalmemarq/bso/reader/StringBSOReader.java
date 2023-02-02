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
    private final StringReader reader;

    public StringBSOReader() {
        this.reader = new StringReader("");
    }

    public BSOMap readMap(String sbso) {
        BSOElement el = this.read(sbso);
        try {
            return (BSOMap) el;
        } catch (ClassCastException e) {
            throw new RuntimeException("Top level element must be BSOMap but found " + el.getType().getName());
        }
    }

    public BSOElement read(String sbso) {
        this.reader.setDataAndReset(sbso);
        return this.readElement();
    }
    
    private BSOElement readNumber() {
        reader.skipWhitespace();

        int curStart = reader.getCursor();

        while (reader.canRead() && reader.isAllowedInNumber(reader.currentChar())) {
            reader.advance();
        }

        if (curStart == reader.getCursor()) {
            throw reader.createErrorWithPos("Expect number but found nothing :(");
        }

        if (reader.currentChar() == 'b') {
            reader.setCursor(curStart);
            byte vl = reader.readByte();
            reader.advance();
            return BSOByte.of(vl);
        } else if (reader.currentChar() == 's') {
            reader.setCursor(curStart);
            short vl = reader.readShort();
            reader.advance();
            return BSOShort.of(vl);
        } else if (reader.currentChar() == 'L') {
            reader.setCursor(curStart);
            long vl = reader.readLong();
            reader.advance();
            return BSOLong.of(vl);
        } else if (reader.currentChar() == 'f') {
            reader.setCursor(curStart);
            float vl = reader.readFloat();
            reader.advance();
            return BSOFloat.of(vl);
        } else if (reader.currentChar() == 'd' || reader.currentChar() == 'D') {
            reader.setCursor(curStart);
            double vl = reader.readDouble();
            reader.advance();
            return BSODouble.of(vl);
        } else {
            reader.setCursor(curStart);
            int vl = reader.readInt();
            return BSOInt.of(vl);
        }
    }

    private BSOMap readMap() {
        BSOMap map = new BSOMap();

        reader.skipWhitespace();
        reader.expect('{');
        reader.skipWhitespace();

        while (reader.canRead() && reader.currentChar() != '}') {
            String key = reader.readString();
            reader.skipWhitespace();
            reader.expect(':');
            reader.skipWhitespace();
            map.put(key, this.readElement());
            reader.skipWhitespace();

            if (reader.canRead()) {
                if (reader.currentChar() == ',') {
                    reader.advance();
                    if (reader.currentChar() == '}') {
                        throw reader.createErrorWithPos("Trailing comma");
                    }
                } else if (reader.currentChar() != '}') {
                    throw reader.createErrorWithPos("Missing comma");
                }
            } else {
                throw reader.createErrorWithPos("Map ended badly");
            }

            reader.skipWhitespace();
        }

        reader.expect('}');
        return map;
    }

    private BSOList readList() {
        List<BSOElement> list = new ArrayList<>();

        reader.skipWhitespace();
        reader.expect('[');
        reader.skipWhitespace();

        BSOType<?> type = null;
        while (reader.canRead() && reader.currentChar() != ']') {
            BSOElement el = this.readElement();
            if (type == null) {
                type = el.getType();
            } else if (type != el.getType()) {
                throw reader.createErrorWithPos("List can only contain a single type. Found element of type %s when list is of type %s", el.getType().getName(), type.getName());
            }

            list.add(el);

            reader.skipWhitespace();

            if (reader.canRead()) {
                if (reader.currentChar() == ',') {
                    reader.advance();
                    reader.skipWhitespace();

                    if (reader.currentChar() == ']') {
                        throw reader.createErrorWithPos("Trailing comma");
                    }
                } else if (reader.currentChar() != ']') {
                    throw reader.createErrorWithPos("Missing comma");
                }
            } else {
                throw reader.createErrorWithPos("List ended badly");
            }
        }

        reader.expect(']');
        return new BSOList(list);
    }

    private BSOByteArray readByteArray() {
        List<Byte> list = new ArrayList<>();

        reader.skipWhitespace();
        reader.expect('[');
        reader.expect('B');
        reader.expect(';');
        reader.skipWhitespace();

        while (reader.canRead() && reader.currentChar() != ']') {
            byte num = reader.readByte();
            list.add(num);

            if (reader.currentChar() == 'b') { // Skip if number had the indicator. It's not a problem
                reader.advance();
            }

            reader.skipWhitespace();

            if (reader.canRead()) {
                if (reader.currentChar() == ',') {
                    reader.advance();
                    reader.skipWhitespace();

                    if (reader.currentChar() == ']') {
                        throw reader.createErrorWithPos("Trailing comma");
                    }
                } else if (reader.currentChar() != ']') {
                    throw reader.createErrorWithPos("Missing comma");
                }
            } else {
                throw reader.createErrorWithPos("ByteArray ended badly");
            }
        }

        reader.expect(']');
        reader.skipWhitespace();
        return BSOByteArray.of(list);
    }

    private BSOShortArray readShortArray() {
        List<Short> list = new ArrayList<>();

        reader.skipWhitespace();
        reader.expect('[');
        reader.expect('S');
        reader.expect(';');
        reader.skipWhitespace();

        while (reader.canRead() && reader.currentChar() != ']') {
            short num = reader.readShort();
            list.add(num);

            if (reader.currentChar() == 's') { // Skip if number had the indicator. It's not a problem
                reader.advance();
            }

            reader.skipWhitespace();

            if (reader.canRead()) {
                if (reader.currentChar() == ',') {
                    reader.advance();
                    reader.skipWhitespace();

                    if (reader.currentChar() == ']') {
                        throw reader.createErrorWithPos("Trailing comma");
                    }
                } else if (reader.currentChar() != ']') {
                    throw reader.createErrorWithPos("Missing comma");
                }
            } else {
                throw reader.createErrorWithPos("ShortArray ended badly");
            }
        }

        reader.expect(']');
        reader.skipWhitespace();
        return BSOShortArray.of(list);
    }

    private BSOIntArray readIntArray() {
        List<Integer> list = new ArrayList<>();

        reader.skipWhitespace();
        reader.expect('[');
        reader.expect('I');
        reader.expect(';');
        reader.skipWhitespace();

        while (reader.canRead() && reader.currentChar() != ']') {
            int num = reader.readInt();
            list.add(num);
            reader.skipWhitespace();

            if (reader.canRead()) {
                if (reader.currentChar() == ',') {
                    reader.advance();
                    reader.skipWhitespace();

                    if (reader.currentChar() == ']') {
                        throw reader.createErrorWithPos("Trailing comma");
                    }
                } else if (reader.currentChar() != ']') {
                    throw reader.createErrorWithPos("Missing comma");
                }
            } else {
                throw reader.createErrorWithPos("IntArray ended badly");
            }
        }

        reader.expect(']');
        reader.skipWhitespace();
        return BSOIntArray.of(list);
    }

    private BSOLongArray readLongArray() {
        List<Long> list = new ArrayList<>();

        reader.skipWhitespace();
        reader.expect('[');
        reader.expect('L');
        reader.expect(';');
        reader.skipWhitespace();

        while (reader.canRead() && reader.currentChar() != ']') {
            long num = reader.readLong();
            list.add(num);

            if (reader.currentChar() == 'L') { // Skip if number had the indicator. It's not a problem
                reader.advance();
            }

            reader.skipWhitespace();

            if (reader.canRead()) {
                if (reader.currentChar() == ',') {
                    reader.advance();
                    reader.skipWhitespace();

                    if (reader.currentChar() == ']') {
                        throw reader.createErrorWithPos("Trailing comma");
                    }
                } else if (reader.currentChar() != ']') {
                    throw reader.createErrorWithPos("Missing comma");
                }
            } else {
                throw reader.createErrorWithPos("LongArray ended badly");
            }
        }

        reader.expect(']');
        reader.skipWhitespace();
        return BSOLongArray.of(list);
    }

    private BSOFloatArray readFloatArray() {
        List<Float> list = new ArrayList<>();

        reader.skipWhitespace();
        reader.expect('[');
        reader.expect('F');
        reader.expect(';');
        reader.skipWhitespace();

        while (reader.canRead() && reader.currentChar() != ']') {
            float num = reader.readFloat();
            list.add(num);

            if (reader.currentChar() == 'f') { // Skip if number had the indicator. It's not a problem
                reader.advance();
            }

            reader.skipWhitespace();

            if (reader.canRead()) {
                if (reader.currentChar() == ',') {
                    reader.advance();
                    reader.skipWhitespace();

                    if (reader.currentChar() == ']') {
                        throw reader.createErrorWithPos("Trailing comma");
                    }
                } else if (reader.currentChar() != ']') {
                    throw reader.createErrorWithPos("Missing comma");
                }
            } else {
                throw reader.createErrorWithPos("FloatArray ended badly");
            }
        }

        reader.expect(']');
        reader.skipWhitespace();
        return BSOFloatArray.of(list);
    }

    private BSODoubleArray readDoubleArray() {
        List<Double> list = new ArrayList<>();

        reader.skipWhitespace();
        reader.expect('[');
        reader.expect('D');
        reader.expect(';');
        reader.skipWhitespace();

        while (reader.canRead() && reader.currentChar() != ']') {
            double num = reader.readDouble();
            list.add(num);

            if (reader.currentChar() == 'd' || reader.currentChar() == 'D') { // Skip if number had the indicator. It's not a problem
                reader.advance();
            }

            reader.skipWhitespace();

            if (reader.canRead()) {
                if (reader.currentChar() == ',') {
                    reader.advance();
                    reader.skipWhitespace();

                    if (reader.currentChar() == ']') {
                        throw reader.createErrorWithPos("Trailing comma");
                    }
                } else if (reader.currentChar() != ']') {
                    throw reader.createErrorWithPos("Missing comma");
                }
            } else {
                throw reader.createErrorWithPos("DoubleArray ended badly");
            }
        }

        reader.expect(']');
        reader.skipWhitespace();
        return BSODoubleArray.of(list);
    }

    private BSOElement readElement() {
        reader.skipWhitespace();

        if (this.reader.currentChar() == '{') {
            return this.readMap();
        } else if (this.reader.currentChar() == '[' && reader.peek(1) == 'B') {
            return this.readByteArray();
        } else if (this.reader.currentChar() == '[' && reader.peek(1) == 'S') {
            return this.readShortArray();
        } else if (this.reader.currentChar() == '[' && reader.peek(1) == 'I') {
            return this.readIntArray();
        } else if (this.reader.currentChar() == '[' && reader.peek(1) == 'L') {
            return this.readLongArray();
        } else if (this.reader.currentChar() == '[' && reader.peek(1) == 'F') {
            return this.readFloatArray();
        } else if (this.reader.currentChar() == '[' && reader.peek(1) == 'D') {
            return this.readDoubleArray();
        } else if (this.reader.currentChar() == '[') {
            return this.readList();
        } else if (this.reader.currentChar() == '"' || this.reader.currentChar() == '\'') {
            return BSOString.of(reader.readQuotedString());
        } else if (reader.isAllowedInNumber(this.reader.currentChar())) {
            return this.readNumber();
        } else {
            throw reader.createErrorWithShowStarting("Unknown type started with ", 5);
        }
    }
}
