package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.annotation.Nullable;

import me.kalmemarq.bso.number.BSOByte;
import me.kalmemarq.bso.number.BSODouble;
import me.kalmemarq.bso.number.BSOFloat;
import me.kalmemarq.bso.number.BSOInt;
import me.kalmemarq.bso.number.BSOLong;
import me.kalmemarq.bso.number.BSOShort;

public class BSOMap implements BSOElement {
    protected final Map<String, BSOElement> entries;
    protected boolean indefiniteLength = true;

    public BSOMap() {
        this(new HashMap<>());
    }

    public BSOMap(Map<String, BSOElement> map) {
        this.entries = map;
    }

    public static BSOMap of(Map<String, BSOElement> map) {
        return new BSOMap(map);
    }

    public void setIndefiniteLength(boolean use) {
        this.indefiniteLength = use;
    }

    @Override
    public BSOType<BSOMap> getType() {
        return BSOTypes.MAP;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        if (!indefiniteLength) {
            BSOUtils.writeLength(output, this.entries.size());
        }

        for (Entry<String, BSOElement> entry : this.entries.entrySet()) {
            output.writeByte(entry.getValue().getTypeId() + entry.getValue().getAdditionalData());
            output.writeUTF(entry.getKey());
            entry.getValue().write(output);
        }

        if (indefiniteLength) output.writeByte(END_TYPE_ID);
    }

    @Override
    public int getAdditionalData() {
        return BSOUtils.lengthAdditionalData(this.size(), this.indefiniteLength);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitMap(this);
    }

    @Override
    public BSOElement copy() {
        Map<String, BSOElement> map = new HashMap<>();
        for (Entry<String, BSOElement> entry : this.entries.entrySet()) {
            map.put(entry.getKey(), entry.getValue().copy());
        }
        return new BSOMap(map);
    }

    public Set<String> keys() {
        return this.entries.keySet();
    }
    
    public Collection<BSOElement> values() {
        return this.entries.values();
    }

    public Set<Map.Entry<String, BSOElement>> entries() {
        return this.entries.entrySet();
    }

    public int size() {
        return this.entries.size();
    }

    private byte getType(String key) {
        BSOElement el = this.entries.get(key);
        if (el == null) return BSOTypes.NULL.getId();
        return el.getTypeId();
    }

    public boolean contains(String key) {
        return this.entries.containsKey(key);
    }

    public boolean contains(String key, BSOType<?> type) {
        return this.contains(key, type.getId());
    }

    public boolean contains(String key, int type) {
        return this.getType(key) == type;
    }

    public void put(String key, BSOElement value) {
        this.entries.put(key, value);
    }

    public void put(String key, byte value) {
        this.entries.put(key, BSOByte.of(value));
    }

    public void put(String key, boolean value) {
        this.entries.put(key, BSOByte.of(value));
    }

    public void put(String key, short value) {
        this.entries.put(key, BSOShort.of(value));
    }

    public void put(String key, int value) {
        this.entries.put(key, BSOInt.of(value));
    }

    public void put(String key, long value) {
        this.entries.put(key, BSOLong.of(value));
    }
    
    public void put(String key, float value) {
        this.entries.put(key, BSOFloat.of(value));
    }

    public void put(String key, double value) {
        this.entries.put(key, BSODouble.of(value));
    }

    public void put(String key, String value) {
        this.entries.put(key, BSOString.of(value));
    }

    public void put(String key, byte[] value) {
        this.entries.put(key, BSOByteArray.of(value));
    }

    public void putByteArray(String key, boolean[] value) {
        this.entries.put(key, BSOByteArray.of(value));
    }

    public void putByteArray(String key, List<Byte> value) {
        this.entries.put(key, BSOByteArray.of(value));
    }

    public void putShortArray(String key, short[] value) {
        this.entries.put(key, BSOShortArray.of(value));
    }

    public void putShortArray(String key, List<Short> value) {
        this.entries.put(key, BSOShortArray.of(value));
    }
    
    public void putIntArray(String key, int[] value) {
        this.entries.put(key, BSOIntArray.of(value));
    }

    public void putIntArray(String key, List<Integer> value) {
        this.entries.put(key, BSOIntArray.of(value));
    }
    
    public void putLongArray(String key, long[] value) {
        this.entries.put(key, BSOLongArray.of(value));
    }

    public void putLongArray(String key, List<Long> value) {
        this.entries.put(key, BSOLongArray.of(value));
    }

    public void putFloatArray(String key, float[] value) {
        this.entries.put(key, BSOFloatArray.of(value));
    }

    public void putFloatArray(String key, List<Float> value) {
        this.entries.put(key, BSOFloatArray.of(value));
    }

    public void putDoubleArray(String key, double[] value) {
        this.entries.put(key, BSODoubleArray.of(value));
    }

    public void putDoubleArray(String key, List<Double> value) {
        this.entries.put(key, BSODoubleArray.of(value));
    }

    public byte getByte(String key) {
        if (this.contains(key, BSOTypes.BYTE)) return ((BSOByte)this.entries.get(key)).getValue();
        return 0;
    }
    
    public boolean getBoolean(String key) {
        return getByte(key) != 0;
    }

    public short getShort(String key) {
        if (this.contains(key, BSOTypes.SHORT)) return ((BSOShort)this.entries.get(key)).getValue();
        return 0;
    }

    public int getInt(String key) {
        if (this.contains(key, BSOTypes.INT)) return ((BSOInt)this.entries.get(key)).getValue();
        return 0;
    }

    public long getLong(String key) {
        if (this.contains(key, BSOTypes.LONG)) return ((BSOLong)this.entries.get(key)).getValue();
        return 0;
    }

    public float getFloat(String key) {
        if (this.contains(key, BSOTypes.FLOAT)) return ((BSOFloat)this.entries.get(key)).getValue();
        return 0;
    }

    public double getDouble(String key) {
        if (this.contains(key, BSOTypes.DOUBLE)) return ((BSODouble)this.entries.get(key)).getValue();
        return 0;
    }

    public String getString(String key) {
        if (this.contains(key, BSOTypes.STRING)) return ((BSOString)this.entries.get(key)).getValue();
        return "";
    }

    public byte[] getByteArray(String key) {
        if (this.contains(key, BSOTypes.BYTE_ARRAY)) return ((BSOByteArray)this.entries.get(key)).getByteArray();
        return new byte[0];
    }

    public short[] getShortArray(String key) {
        if (this.contains(key, BSOTypes.SHORT_ARRAY)) return ((BSOShortArray)this.entries.get(key)).getShortArray();
        return new short[0];
    }

    public int[] getIntArray(String key) {
        if (this.contains(key, BSOTypes.INT_ARRAY)) return ((BSOIntArray)this.entries.get(key)).getIntArray();
        return new int[0];
    }

    public long[] getLongArray(String key) {
        if (this.contains(key, BSOTypes.LONG_ARRAY)) return ((BSOLongArray)this.entries.get(key)).getLongArray();
        return new long[0];
    }

    public float[] getFloatArray(String key) {
        if (this.contains(key, BSOTypes.FLOAT_ARRAY)) return ((BSOFloatArray)this.entries.get(key)).getFloatArray();
        return new float[0];
    }

    public double[] getDoubleArray(String key) {
        if (this.contains(key, BSOTypes.DOUBLE_ARRAY)) return ((BSODoubleArray)this.entries.get(key)).getDoubleArray();
        return new double[0];
    }

    public BSOMap getMap(String key) {
        if (this.contains(key, BSOTypes.MAP)) return (BSOMap)this.entries.get(key);
        return new BSOMap();
    }

    public BSOList getList(String key) {
        if (this.contains(key, BSOTypes.LIST)) return (BSOList)this.entries.get(key);
        return new BSOList();
    }

    @Nullable
    public BSOElement get(String key) {
        return this.entries.get(key);
    }

    @Nullable
    public BSOElement remove(String key) {
        return this.entries.remove(key);
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BSOMap)) return false;
        return Objects.equals(this.entries, ((BSOMap)obj).entries);
    }

    @Override
    public int hashCode() {
        return this.entries.hashCode();
    }
    
    @Override
    public String toString() {
        return this.asString();
    }
}
