package me.kalmemarq.bso;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.kalmemarq.bso.number.BSOByte;
import me.kalmemarq.bso.number.BSODouble;
import me.kalmemarq.bso.number.BSOFloat;
import me.kalmemarq.bso.number.BSOInt;
import me.kalmemarq.bso.number.BSOLong;
import me.kalmemarq.bso.number.BSOShort;

public class BSOTypes {
    protected static final Map<Byte, BSOType<?>> TYPES = new HashMap<>();

    public static final BSOType<BSONull> NULL = new BSOType<>(BSOElement.NULL_TYPE_ID, "TAG_Null") {
        @Override
        public BSONull read(DataInput input, int additionalData) throws IOException {
            return BSONull.INSTANCE;
        }
    };
    
    public static final BSOType<BSOByte> BYTE = new BSOType<>(BSOElement.BYTE_TYPE_ID, "TAG_Byte") {
        @Override
        public BSOByte read(DataInput input, int additionalData) throws IOException {
            return BSOByte.of(input.readByte());
        }
    };

    public static final BSOType<BSOShort> SHORT = new BSOType<>(BSOElement.SHORT_TYPE_ID, "TAG_Short") {
        @Override
        public BSOShort read(DataInput input, int additionalData) throws IOException {
            return BSOShort.of(input.readShort());
        }
    };

    public static final BSOType<BSOInt> INT = new BSOType<>(BSOElement.INT_TYPE_ID, "TAG_Int") {
        @Override
        public BSOInt read(DataInput input, int additionalData) throws IOException {
            if (additionalData == BSOUtils.VARNUM_BYTE) {
                return BSOInt.of(input.readByte());
            } else if (additionalData == BSOUtils.VARNUM_SHORT) {
                return BSOInt.of(input.readShort());
            }

            return BSOInt.of(input.readInt());
        }
    };

    public static final BSOType<BSOLong> LONG = new BSOType<>(BSOElement.LONG_TYPE_ID, "TAG_Long") {
        @Override
        public BSOLong read(DataInput input, int additionalData) throws IOException {
            if (additionalData == BSOUtils.VARNUM_BYTE) {
                return BSOLong.of(input.readByte());
            } else if (additionalData == BSOUtils.VARNUM_SHORT) {
                return BSOLong.of(input.readShort());
            } else if (additionalData == BSOUtils.VARNUM_INT) {
                return BSOLong.of(input.readInt());
            }

            return BSOLong.of(input.readLong());
        }
    };

    public static final BSOType<BSOFloat> FLOAT = new BSOType<>(BSOElement.FLOAT_TYPE_ID, "TAG_Float") {
        @Override
        public BSOFloat read(DataInput input, int additionalData) throws IOException {
            return BSOFloat.of(input.readFloat());
        }
    };

    public static final BSOType<BSODouble> DOUBLE = new BSOType<>(BSOElement.DOUBLE_TYPE_ID, "TAG_Double") {
        @Override
        public BSODouble read(DataInput input, int additionalData) throws IOException {
            return BSODouble.of(input.readDouble());
        }
    };

    public static final BSOType<BSOString> STRING = new BSOType<>(BSOElement.STRING_TYPE_ID, "TAG_String") {
        @Override
        public BSOString read(DataInput input, int additionalData) throws IOException {
            return BSOString.of(input.readUTF());
        }
    };

    public static final BSOType<BSOMap> MAP = new BSOType<>(BSOElement.MAP_TYPE_ID, "TAG_Map") {
        @Override
        public BSOMap read(DataInput input, int additionalData) throws IOException {
            Map<String, BSOElement> map = new HashMap<>();
            
            if (additionalData == BSOUtils.INDEFINITE_LENGTH) {
                byte b;
                while ((b = input.readByte()) != BSOElement.END_TYPE_ID) {
                    BSOType<?> type = BSOTypes.byId((byte)(b & 0x0F));
                    map.put(input.readUTF(), type.read(input, (byte)(b & 0xF0)));
                }
            } else {
                int len = BSOUtils.readLength(input, additionalData);

                for (int i = 0; i < len; i++) {
                    byte b = input.readByte();
                    BSOType<?> type = BSOTypes.byId((byte)(b & 0x0F));
                    map.put(input.readUTF(), type.read(input, (byte)(b & 0xF0)));
                }
            }

            return BSOMap.of(map);
        }
    };

    public static final BSOType<BSOList> LIST = new BSOType<>(BSOElement.LIST_TYPE_ID, "TAG_List") {
        @Override
        public BSOList read(DataInput input, int additionalData) throws IOException {
            List<BSOElement> list = new ArrayList<>();
            
            int lenType = additionalData & 0x30;

            if (lenType == BSOUtils.INDEFINITE_LENGTH) {
                byte b;
                while ((b = input.readByte()) != BSOElement.END_TYPE_ID) {
                    BSOType<?> type = BSOTypes.byId((byte)(b & 0x0F));
                    list.add(type.read(input, (byte)(b & 0xF0)));
                }
            } else {
                int len = BSOUtils.readLength(input, lenType);
                boolean hasMultipleTypes = (additionalData & 0xC0) == 0x40;

                if (hasMultipleTypes) {
                    for (int i = 0; i < len; i++) {
                        int b = input.readByte();
                        BSOType<?> type = BSOTypes.byId((byte)(b & 0x0F));
                        list.add(type.read(input, b & 0xF0));
                    }
                } else if (len > 0) {
                    BSOType<?> type = BSOTypes.byId((byte)(input.readByte() & 0x0F));

                    for (int i = 0; i < len; i++) {
                        list.add(type.read(input, 0x00));
                    }
                }
            }

            return new BSOList(list);
        }
    };

    public static final BSOType<BSOByteArray> BYTE_ARRAY = new BSOType<>(BSOElement.BYTE_ARRAY_TYPE_ID, "TAG_ByteArray") {
        @Override
        public BSOByteArray read(DataInput input, int additionalData) throws IOException {
            int len = BSOUtils.readLength(input, additionalData);
            byte[] vls = new byte[len];
            for (int i = 0; i < len; i++) {
                vls[i] = input.readByte();
            }
            return BSOByteArray.of(vls);
        }
    };

    public static final BSOType<BSOShortArray> SHORT_ARRAY = new BSOType<>(BSOElement.SHORT_ARRAY_TYPE_ID, "TAG_ShortArray") {
        @Override
        public BSOShortArray read(DataInput input, int additionalData) throws IOException {
            int len = BSOUtils.readLength(input, additionalData);
            short[] vls = new short[len];
            for (int i = 0; i < len; i++) {
                vls[i] = input.readShort();
            }
            return BSOShortArray.of(vls);
        }
    };

    public static final BSOType<BSOIntArray> INT_ARRAY = new BSOType<>(BSOElement.INT_ARRAY_TYPE_ID, "TAG_IntArray") {
        @Override
        public BSOIntArray read(DataInput input, int additionalData) throws IOException {
            int len = BSOUtils.readLength(input, additionalData);
            int[] vls = new int[len];
            for (int i = 0; i < len; i++) {
                vls[i] = input.readInt();
            }
            return BSOIntArray.of(vls);
        }
    };

    public static final BSOType<BSOLongArray> LONG_ARRAY = new BSOType<>(BSOElement.LONG_ARRAY_TYPE_ID, "TAG_LongArray") {
        @Override
        public BSOLongArray read(DataInput input, int additionalData) throws IOException {
            int len = BSOUtils.readLength(input, additionalData);
            long[] vls = new long[len];
            for (int i = 0; i < len; i++) {
                vls[i] = input.readLong();
            }
            return BSOLongArray.of(vls);
        }
    };

    public static final BSOType<BSOFloatArray> FLOAT_ARRAY = new BSOType<>(BSOElement.FLOAT_ARRAY_TYPE_ID, "TAG_FloatArray") {
        @Override
        public BSOFloatArray read(DataInput input, int additionalData) throws IOException {
            int len = BSOUtils.readLength(input, additionalData);
            float[] vls = new float[len];
            for (int i = 0; i < len; i++) {
                vls[i] = input.readFloat();
            }
            return BSOFloatArray.of(vls);
        }
    };

    public static final BSOType<BSODoubleArray> DOUBLE_ARRAY = new BSOType<>(BSOElement.DOUBLE_ARRAY_TYPE_ID, "TAG_DoubleArray") {
        @Override
        public BSODoubleArray read(DataInput input, int additionalData) throws IOException {
            int len = BSOUtils.readLength(input, additionalData);
            double[] vls = new double[len];
            for (int i = 0; i < len; i++) {
                vls[i] = input.readDouble();
            }
            return BSODoubleArray.of(vls);
        }
    };

    private BSOTypes() {}

    public static BSOType<?> byId(byte id) {
        return TYPES.getOrDefault(id, NULL);
    }
}
