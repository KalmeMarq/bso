package me.kalmemarq.bso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.DeflaterInputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class BsoIo {
     private static final byte[] HEADER = new byte[]{0x42, 0x53, 0x4F, 0};

    public static void write(Path path, BsoElement element) {
        write(path, element, Compression.NONE, Endianess.BIG);
    }

    public static void write(Path path, BsoElement element, Compression compression) {
        write(path, element, compression, Endianess.BIG);
    }

    public static void write(Path path, BsoElement element, Compression compression, Endianess endianess) {
        try (BufferedOutputStream outputStream = new BufferedOutputStream(Files.newOutputStream(path));
             EndianBasedDataOutputStream output = new EndianBasedDataOutputStream(
                     compression == Compression.GZIP ? new GZIPOutputStream(outputStream)
                             : compression == Compression.ZLIB ? new DeflaterOutputStream(outputStream) : outputStream
                     , endianess)) {
            output.write(HEADER);
            output.writeByte(element.getId() | element.getAdditionalData() << 4);
            element.write(output);
        } catch (IOException ignored) {
        }
    }

    public static BsoElement read(Path path) {
        return read(path, Compression.NONE, Endianess.BIG);
    }

    public static BsoElement read(Path path, Compression compression) {
        return read(path, compression, Endianess.BIG);
    }

    public static BsoElement read(Path path, Compression compression, Endianess endianess) {
        try (BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(path));
             EndianBasedDataInputStream input = new EndianBasedDataInputStream(
                     compression == Compression.GZIP ? new GZIPInputStream(inputStream)
                             : compression == Compression.ZLIB ? new DeflaterInputStream(inputStream) : inputStream
                     , endianess)) {
            byte[] header = new byte[HEADER.length];
            if (inputStream.read(header) != HEADER.length || !Arrays.equals(header, HEADER)) return null;
            int idAd = input.readByte();
            return read(input, idAd & 0xF, (idAd >> 4) & 0xF);
        } catch (IOException ignored) {
        }
        return null;
    }

    private static BsoElement read(DataInput input, int id, int ad) throws IOException {
        if (id == 1) {
            if (ad == 0b0010) {
                return BsoBoolean.FALSE;
            } else if (ad == 0b0001) {
                return BsoBoolean.TRUE;
            } else {
                return new BsoByte(input.readByte());
            }
        } else if (id == 2) {
            if (ad == 0b0001) {
                return new BsoShort(input.readByte());
            } else {
                return new BsoShort(input.readShort());
            }
        } else if (id == 3) {
            if (ad == 0b0001) {
                return new BsoInt(input.readByte());
            } else if (ad == 0b0010) {
                return new BsoInt(input.readShort());
            } else {
                return new BsoInt(input.readInt());
            }
        } else if (id == 4) {
            if (ad == 0b0001) {
                return new BsoLong(input.readByte());
            } else if (ad == 0b0010) {
                return new BsoLong(input.readShort());
            } else if (ad == 0b0011) {
                return new BsoLong(input.readInt());
            } else {
                return new BsoLong(input.readLong());
            }
        } else if (id == 5) {
            if (ad == 0b0001) {
                return new BsoDouble(input.readDouble());
            } else {
                return new BsoFloat(input.readFloat());
            }
        } else if (id == 6) {
            int sizeAd = (ad & 0b11) & 0b0011;
            return new BsoString(BsoUtils.readUTF(input, sizeAd == 0b01 ? 0 : sizeAd == 0b10 ? 1 : 2, false));
        } else if (id == 7) {
            int sizeAd = (ad & 0b11) & 0b0011;
            int size = sizeAd == 0b01 ? input.readUnsignedByte() : sizeAd == 0b10 ? input.readUnsignedShort() : input.readInt();
            BsoMap map = new BsoMap();

            for (int i = 0; i < size; ++i) {
                int elId = input.readByte();
                String key = BsoUtils.readUTF(input, 0, false);
                BsoElement element = read(input, elId & 0xF, (elId >> 4) & 0xF);
                map.put(key, element);
            }

            return map;
        } else if (id == 8) {
            int sizeAd = (ad & 0b11) & 0b0011;
            int size = sizeAd == 0b01 ? input.readUnsignedByte() : sizeAd == 0b10 ? input.readUnsignedShort() : input.readInt();
            List<BsoElement> list = new ArrayList<>(size);

            for (int i = 0; i < size; ++i) {
                int elId = input.readByte();
                BsoElement element = read(input, elId & 0xF, (elId >> 4) & 0xF);
                list.add(element);
            }

            return new BsoList(list);
        } else if (id == 9) {
            int sizeAd = (ad & 0b11) & 0b0011;
            int size = sizeAd == 0b01 ? input.readUnsignedByte() : sizeAd == 0b10 ? input.readUnsignedShort() : input.readInt();

            if ((ad >> 2) == 0b01) {
                boolean[] array = new boolean[size];
                for (int i = 0; i < array.length; ++i) {
                    int b = input.readByte();
                    for (int j = 0; j < 4; ++j) {
                        if (i + j >= array.length) break;
                        array[i + j] = ((b >> (3 - j)) & 0b1) != 0;
                    }
                    i += 4;
                }
                return new BsoBooleanArray(array);
            } else {
                byte[] array = new byte[size];
                for (int i = 0; i < array.length; ++i) {
                    array[i] = input.readByte();
                }
                return new BsoByteArray(array);
            }
        } else if (id == 10) {
            int sizeAd = (ad & 0b11) & 0b0011;
            int size = sizeAd == 0b01 ? input.readUnsignedByte() : sizeAd == 0b10 ? input.readUnsignedShort() : input.readInt();
            short[] array = new short[size];
            int rangeAd = ((ad >> 2) & 0b11);
            for (int i = 0; i < array.length; ++i) {
                array[i] = rangeAd == 0b01 ? input.readByte() : input.readShort();
            }
            return new BsoShortArray(array);
        } else if (id == 11) {
            int sizeAd = (ad & 0b11) & 0b0011;
            int size = sizeAd == 0b01 ? input.readUnsignedByte() : sizeAd == 0b10 ? input.readUnsignedShort() : input.readInt();
            int[] array = new int[size];
            int rangeAd = ((ad >> 2) & 0b11);
            for (int i = 0; i < array.length; ++i) {
                array[i] = rangeAd == 0b01 ? input.readByte() : rangeAd == 0b10 ? input.readShort() : input.readInt();
            }
            return new BsoIntArray(array);
        } else if (id == 12) {
            int sizeAd = (ad & 0b11) & 0b0011;
            int size = sizeAd == 0b01 ? input.readUnsignedByte() : sizeAd == 0b10 ? input.readUnsignedShort() : input.readInt();
            long[] array = new long[size];
            int rangeAd = ((ad >> 2) & 0b11);
            for (int i = 0; i < array.length; ++i) {
                array[i] = rangeAd == 0b01 ? input.readByte() : rangeAd == 0b10 ? input.readShort() : rangeAd == 0b11 ? input.readInt() : input.readLong();
            }
            return new BsoLongArray(array);
        } else if (id == 13) {
            int sizeAd = (ad & 0b11) & 0b0011;
            int size = sizeAd == 0b01 ? input.readUnsignedByte() : sizeAd == 0b10 ? input.readUnsignedShort() : input.readInt();
            boolean isDouble = ((ad >> 2) & 0b11) == 0b01;
            if (isDouble) {
                double[] array = new double[size];
                for (int i = 0; i < array.length; ++i) {
                    array[i] = input.readDouble();
                }
                return new BsoDoubleArray(array);
            } else {
                float[] array = new float[size];
                for (int i = 0; i < array.length; ++i) {
                    array[i] = input.readFloat();
                }
                return new BsoFloatArray(array);
            }
        }
        return null;
    }

    public static <T extends BsoElement> T read(Path path, Class<T> clazz) {
        return read(path, Compression.NONE, Endianess.BIG, clazz);
    }

    public static <T extends BsoElement> T read(Path path, Compression compression, Class<T> clazz) {
        return read(path, compression, Endianess.BIG, clazz);
    }

    @SuppressWarnings("unchecked")
    public static <T extends BsoElement> T read(Path path, Compression compression, Endianess endianess, Class<T> clazz) {
        BsoElement element = read(path, compression, endianess);
        return element != null && element.getClass() == clazz ? (T) element : null;
    }

    public enum Compression {
        NONE,
        GZIP,
        ZLIB
    }

    public enum Endianess {
        BIG,
        LITTLE
    }
}
