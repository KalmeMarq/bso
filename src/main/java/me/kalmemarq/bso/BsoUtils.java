package me.kalmemarq.bso;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.UTFDataFormatException;

class BsoUtils {
    public static boolean isShortInByteRange(short value) {
        return (value & ~0x7F) == 0 || (value & ~0x7F) == ~0x7F;
    }

    public static boolean isIntInByteRange(int value) {
        return (value & ~0x7F) == 0 || (value & ~0x7F) == ~0x7F;
    }

    public static boolean isIntInShortRange(int value) {
        return (value & ~0x7FFF) == 0 || (value & ~0x7FFF) == ~0x7FFF;
    }

    public static boolean isLongInByteRange(long value) {
        return (value & ~0x7FL) == 0L || (value & ~0x7FL) == ~0x7FL;
    }

    public static boolean isLongInShortRange(long value) {
        return (value & ~0x7FFFL) == 0L || (value & ~0x7FFFL) == ~0x7FFFL;
    }

    public static boolean isLongInIntRange(long value) {
        return (value & ~0x7FFFFFFFL) == 0L || (value & ~0x7FFFFFFFL) == ~0x7FFFFFFFL;
    }

    public static int getUTFLength(String str) {
        int strlen = str.length();
        int utflen = 0;
        for (int i = 0; i < strlen; i++) {
            int c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                utflen++;
            } else if (c > 0x07FF) {
                utflen += 3;
            } else {
                utflen += 2;
            }
        }
        return utflen;
    }

    public static int writeUTF(String str, DataOutput out, int sizeType, boolean isSigned) throws IOException {
        int strlen = str.length();
        int utflen = getUTFLength(str);
        int count = 0;

        int range = sizeType == 0 ? (isSigned ? Byte.MAX_VALUE : 255) : sizeType == 1 ? (isSigned ? Short.MAX_VALUE : 65535) : Integer.MAX_VALUE;

        if (utflen > range)
            throw new UTFDataFormatException("encoded string too long: " + utflen + " bytes");

        int sizeLength = sizeType == 0 ? 1 : sizeType == 1 ? 2 : 4;
        byte[] bytearr = new byte[utflen+sizeLength];

        if (sizeType == 1) {
            bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
        } else if (sizeType == 2) {
            bytearr[count++] = (byte) ((utflen >>> 24) & 0xFF);
            bytearr[count++] = (byte) ((utflen >>> 16) & 0xFF);
            bytearr[count++] = (byte) ((utflen >>> 8) & 0xFF);
        }
        bytearr[count++] = (byte) ((utflen) & 0xFF);

        int i, c;
        for (i = 0; i < strlen; i++) {
            c = str.charAt(i);
            if (!((c >= 0x0001) && (c <= 0x007F))) break;
            bytearr[count++] = (byte) c;
        }

        for (;i < strlen; i++){
            c = str.charAt(i);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                bytearr[count++] = (byte) c;
            } else if (c > 0x07FF) {
                bytearr[count++] = (byte) (0xE0 | ((c >> 12) & 0x0F));
                bytearr[count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
                bytearr[count++] = (byte) (0x80 | ((c) & 0x3F));
            } else {
                bytearr[count++] = (byte) (0xC0 | ((c >> 6) & 0x1F));
                bytearr[count++] = (byte) (0x80 | ((c) & 0x3F));
            }
        }
        out.write(bytearr, 0, utflen+sizeLength);
        return utflen + sizeLength;
    }

    public static String readUTF(DataInput in, int sizeType, boolean isSigned) throws IOException {
        int utflen = sizeType == 0 ? isSigned ? in.readByte() : in.readUnsignedByte() : sizeType == 1 ? isSigned ? in.readShort() : in.readUnsignedShort() : in.readInt();
        byte[] bytearr = new byte[utflen];
        char[] chararr =  new char[utflen];

        int c, char2, char3;
        int count = 0;
        int chararr_count = 0;

        in.readFully(bytearr, 0, utflen);

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            if (c > 127) break;
            count++;
            chararr[chararr_count++]=(char)c;
        }

        while (count < utflen) {
            c = (int) bytearr[count] & 0xff;
            switch (c >> 4) {
                case 0: case 1: case 2: case 3: case 4: case 5: case 6: case 7:
                    /* 0xxxxxxx*/
                    count++;
                    chararr[chararr_count++]=(char)c;
                    break;
                case 12: case 13:
                    /* 110x xxxx   10xx xxxx*/
                    count += 2;
                    if (count > utflen)
                        throw new UTFDataFormatException("malformed input: partial character at end");
                    char2 = (int) bytearr[count-1];
                    if ((char2 & 0xC0) != 0x80)
                        throw new UTFDataFormatException("malformed input around byte " + count);
                    chararr[chararr_count++]=(char)(((c & 0x1F) << 6) |
                            (char2 & 0x3F));
                    break;
                case 14:
                    /* 1110 xxxx  10xx xxxx  10xx xxxx */
                    count += 3;
                    if (count > utflen)
                        throw new UTFDataFormatException("malformed input: partial character at end");
                    char2 = (int) bytearr[count-2];
                    char3 = (int) bytearr[count-1];
                    if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80))
                        throw new UTFDataFormatException("malformed input around byte " + (count-1));
                    chararr[chararr_count++]=(char)(((c     & 0x0F) << 12) |
                            ((char2 & 0x3F) << 6)  |
                            ((char3 & 0x3F)));
                    break;
                default:
                    /* 10xx xxxx,  1111 xxxx */
                    throw new UTFDataFormatException("malformed input around byte " + count);
            }
        }
        // The number of chars produced may be less than utflen
        return new String(chararr, 0, chararr_count);
    }

    public static void appendRepeat(StringBuilder builder, char chr, int times) {
        for (int i = 0; i < times; ++i) builder.append(chr);
    }

    public static String escapeNameForQuote(String content) {
        StringBuilder builder = new StringBuilder();
        int cursor = 0;
        while (cursor < content.length()) {
            if (content.charAt(cursor) == '`') {
                builder.append('\\');
            }
            builder.append(content.charAt(cursor));
            ++cursor;
        }
        return builder.toString();
    }
}
