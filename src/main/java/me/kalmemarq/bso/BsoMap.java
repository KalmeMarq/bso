package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BsoMap implements BsoElement {
    private final Map<String, BsoElement> entries;

    public BsoMap() {
        this(new HashMap<>());
    }

    public BsoMap(Map<String, BsoElement> map) {
        this.entries = map;
    }

    @Override
    public int getId() {
        return 0b0111;
    }

    @Override
    public int getAdditionalData() {
        int size = this.entries.size();
        return size <= 255 ? 0b0001 : size <= 65535 ? 0b0010 : 0b0000;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        int size = this.size();
        if (size <= 255) {
            output.writeByte((byte) size);
        } else if (size <= 65535) {
            output.writeShort((short) size);
        } else {
            output.writeInt(size);
        }

        for (Map.Entry<String, BsoElement> entry : this.entries.entrySet()) {
            BsoElement value = entry.getValue();
            output.writeByte(value.getId() | value.getAdditionalData() << 4);
            BsoUtils.writeUTF(entry.getKey(), output, 0, false);
            value.write(output);
        }
    }

    @Override
    public void visit(BsoVisitor visitor) {
        visitor.visitMap(this);
    }

    public void clear() {
        this.entries.clear();
    }

    public int size() {
        return this.entries.size();
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public Set<String> keySet() {
        return this.entries.keySet();
    }

    public Collection<BsoElement> values() {
        return this.entries.values();
    }

    public boolean containsKey(String key) {
        return this.entries.containsKey(key);
    }

    public boolean containsKeyOfType(String key, Class<? extends BsoElement> clazz) {
        return this.entries.containsKey(key) && this.entries.get(key).getClass() == clazz;
    }

    public BsoElement remove(String key) {
        return this.entries.remove(key);
    }

    public BsoElement get(String key) {
        return this.entries.get(key);
    }

    public byte getByte(String key) {
        BsoElement element = this.entries.get(key);
        return element instanceof BsoNumeric ? ((BsoNumeric) element).byteValue() : 0;
    }

    public boolean getBoolean(String key) {
        BsoElement element = this.entries.get(key);
        return element instanceof BsoNumeric && ((BsoNumeric) element).booleanValue();
    }

    public short getShort(String key) {
        BsoElement element = this.entries.get(key);
        return element instanceof BsoNumeric ? ((BsoNumeric) element).shortValue() : 0;
    }

    public int getInt(String key) {
        BsoElement element = this.entries.get(key);
        return element instanceof BsoNumeric ? ((BsoNumeric) element).intValue() : 0;
    }

    public long getLong(String key) {
        BsoElement element = this.entries.get(key);
        return element instanceof BsoNumeric ? ((BsoNumeric) element).longValue() : 0;
    }

    public float getFloat(String key) {
        BsoElement element = this.entries.get(key);
        return element instanceof BsoNumeric ? ((BsoNumeric) element).floatValue() : 0;
    }

    public double getDouble(String key) {
        BsoElement element = this.entries.get(key);
        return element instanceof BsoNumeric ? ((BsoNumeric) element).doubleValue() : 0;
    }

    public String getString(String key) {
        BsoElement element = this.entries.get(key);
        return element instanceof BsoString ? ((BsoString) element).value() : "";
    }

    public BsoMap getMap(String key) {
        BsoElement element = this.entries.get(key);
        return element instanceof BsoMap ? ((BsoMap) element) : new BsoMap();
    }

    public BsoList getList(String key) {
        BsoElement element = this.entries.get(key);
        return element instanceof BsoList ? ((BsoList) element) : new BsoList();
    }

    public byte[] getByteArray(String key) {
        BsoElement element = this.entries.get(key);
        return element instanceof BsoByteArray ? ((BsoByteArray) element).array() : new byte[0];
    }

    public short[] getShortArray(String key) {
        BsoElement element = this.entries.get(key);
        return element instanceof BsoShortArray ? ((BsoShortArray) element).array() : new short[0];
    }

    public int[] getIntArray(String key) {
        BsoElement element = this.entries.get(key);
        return element instanceof BsoIntArray ? ((BsoIntArray) element).array() : new int[0];
    }

    public long[] getLongArray(String key) {
        BsoElement element = this.entries.get(key);
        return element instanceof BsoLongArray ? ((BsoLongArray) element).array() : new long[0];
    }

    public float[] getFloatArray(String key) {
        BsoElement element = this.entries.get(key);
        return element instanceof BsoFloatArray ? ((BsoFloatArray) element).array() : new float[0];
    }

    public double[] getDoubleArray(String key) {
        BsoElement element = this.entries.get(key);
        return element instanceof BsoDoubleArray ? ((BsoDoubleArray) element).array() : new double[0];
    }

    public void put(String key, BsoElement value) {
        this.entries.put(key, value);
    }

    public void put(String key, byte value) {
        this.entries.put(key, new BsoByte(value));
    }

    public void put(String key, boolean value) {
        this.entries.put(key, value ? BsoBoolean.TRUE : BsoBoolean.FALSE);
    }

    public void put(String key, short value) {
        this.entries.put(key, new BsoShort(value));
    }

    public void put(String key, int value) {
        this.entries.put(key, new BsoInt(value));
    }

    public void put(String key, long value) {
        this.entries.put(key, new BsoLong(value));
    }

    public void put(String key, float value) {
        this.entries.put(key, new BsoFloat(value));
    }

    public void put(String key, double value) {
        this.entries.put(key, new BsoDouble(value));
    }

    public void put(String key, String value) {
        this.entries.put(key, new BsoString(value));
    }

    public void put(String key, byte[] value) {
        this.entries.put(key, new BsoByteArray(value));
    }

    public void put(String key, short[] value) {
        this.entries.put(key, new BsoShortArray(value));
    }

    public void put(String key, int[] value) {
        this.entries.put(key, new BsoIntArray(value));
    }

    public void put(String key, long[] value) {
        this.entries.put(key, new BsoLongArray(value));
    }

    public void put(String key, float[] value) {
        this.entries.put(key, new BsoFloatArray(value));
    }

    public void put(String key, double[] value) {
        this.entries.put(key, new BsoDoubleArray(value));
    }

    @Override
    public BsoElement copy() {
        BsoMap map = new BsoMap();
        for (Map.Entry<String, BsoElement> entry : this.entries.entrySet()) {
            map.put(entry.getKey(), entry.getValue().copy());
        }
        return map;
    }

    @Override
    public String toString() {
        return this.asString();
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BsoMap)) return false;
        return Objects.equals(this.entries, ((BsoMap) obj).entries);
    }

    public int hashCode() {
        return this.entries.hashCode();
    }
}
