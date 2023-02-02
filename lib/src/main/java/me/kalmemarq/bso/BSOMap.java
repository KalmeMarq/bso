package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

import javax.annotation.Nullable;

import me.kalmemarq.bso.number.BSOByte;
import me.kalmemarq.bso.number.BSODouble;
import me.kalmemarq.bso.number.BSOFloat;
import me.kalmemarq.bso.number.BSOInt;
import me.kalmemarq.bso.number.BSOLong;
import me.kalmemarq.bso.number.BSOShort;

public class BSOMap implements BSOElement, Iterable<Map.Entry<String, BSOElement>> {
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
            BSOUtil.writeLength(output, this.entries.size());
        }

        for (Entry<String, BSOElement> entry : this.entries.entrySet()) {
            output.writeByte(entry.getValue().getTypeId() + entry.getValue().getAdditionalData());
//            output.writeUTF(entry.getKey());
            BSOUtil.writeIndefiniteUTF8(output, entry.getKey());
            entry.getValue().write(output);
        }

        if (indefiniteLength) output.writeByte(END_TYPE_ID);
    }

    @Override
    public int getAdditionalData() {
        return BSOUtil.lengthAdditionalData(this.size(), this.indefiniteLength);
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

    /**
     * Returns a Set view of the keys contained in this map.
     * @return A set view of the keys contained in this map
     */
    public Set<String> keysSet() {
        return this.entries.keySet();
    }
    
    /**
     * Returns a collection view of the values contained in this map.
     * @return A collection view of the values contained in this map
     */
    public Collection<BSOElement> values() {
        return this.entries.values();
    }

    /**
     * Returns a collection view of the values contained in this map.
     * @return A collection view of the values contained in this map
     */
    public Set<Map.Entry<String, BSOElement>> entrySet() {
        return this.entries.entrySet();
    }

    @Override
    public Iterator<Map.Entry<String, BSOElement>> iterator() {
        return entrySet().iterator();
    }

    /**
     * Performs the given action for each entry in this map until all entries have been processed or the action throws an exception.
     * @param action The action to be performed for each entry
     */
    public void forEach(BiConsumer<? super String, ? super BSOElement> action) {
        this.entries.forEach(action);
    }

    /**
     * Returns the number of entries in this map.
     * @return Number of entries in this map
     */
    public int size() {
        return this.entries.size();
    }

    private byte getType(String key) {
        BSOElement el = this.entries.get(key);
        if (el == null) return BSOTypes.NULL.getId();
        return el.getTypeId();
    }

    /**
     * Returns true if this map contains a mapping for the specified key.
     * @param key The key whose presence in this map is to be tested
     * @return {@code true} if this map contains a mapping for the specified key
     */
    public boolean containsKey(String key) {
        return this.entries.containsKey(key);
    }

    /**
     * Returns true if this map maps one or more keys to the specified value.
     * @param value Value whose presence in this map is to be tested
     * @return {@code true} if this map maps one or more keys to the specified value
     */
    public boolean containsValue(BSOElement value) {
        return this.entries.containsValue(value);
    }

    /**
     * Returns true if map maps contains a mapping for this specified key with value being of the specified type.
     * @param key The key whose presence with type in this map is to be tested
     * @param type The type the value must be
     * @return {@code true} if this map contains a mapping for the specified key with value with the specified type 
     */
    public boolean containsKeyWithType(String key, BSOType<?> type) {
        return this.containsKeyWithType(key, type.getId());
    }

    /**
     * Returns true if map maps contains a mapping for this specified key with value being of the specified type.
     * @param key The key whose presence with type in this map is to be tested
     * @param type The type the value must be
     * @return {@code true} if this map contains a mapping for the specified key with value with the specified type 
     */
    public boolean containsKeyWithType(String key, int type) {
        return this.getType(key) == type;
    }

    /**
     * Associates the specified value with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void put(String key, BSOElement value) {
        this.entries.put(key, value);
    }

    public void putIfAbsent(String key, BSOElement value) {
        this.entries.putIfAbsent(key, value);
    }

    /**
     * Associates the specified byte value with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void put(String key, byte value) {
        this.entries.put(key, BSOByte.of(value));
    }

    /**
     * Associates the specified boolean value with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void put(String key, boolean value) {
        this.entries.put(key, BSOByte.of(value));
    }

    /**
     * Associates the specified short value with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void put(String key, short value) {
        this.entries.put(key, BSOShort.of(value));
    }

    /**
     * Associates the specified int value with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void put(String key, int value) {
        this.entries.put(key, BSOInt.of(value));
    }

    /**
     * Associates the specified long value with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void put(String key, long value) {
        this.entries.put(key, BSOLong.of(value));
    }
    
    /**
     * Associates the specified float value with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void put(String key, float value) {
        this.entries.put(key, BSOFloat.of(value));
    }

    /**
     * Associates the specified double value with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void put(String key, double value) {
        this.entries.put(key, BSODouble.of(value));
    }

    /**
     * Associates the specified string with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void put(String key, String value) {
        this.entries.put(key, BSOString.of(value));
    }

    /**
     * Associates the specified byte array value with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void put(String key, byte[] value) {
        this.entries.put(key, BSOByteArray.of(value));
    }

    /**
     * Associates the specified boolean array with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void putByteArray(String key, boolean[] value) {
        this.entries.put(key, BSOByteArray.of(value));
    }

    /**
     * Associates the specified byte list with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void putByteArray(String key, List<Byte> value) {
        this.entries.put(key, BSOByteArray.of(value));
    }

    /**
     * Associates the specified short array with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void put(String key, short[] value) {
        this.entries.put(key, BSOShortArray.of(value));
    }

    /**
     * Associates the specified short list with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void putShortArray(String key, List<Short> value) {
        this.entries.put(key, BSOShortArray.of(value));
    }
    
    /**
     * Associates the specified int array with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void put(String key, int[] value) {
        this.entries.put(key, BSOIntArray.of(value));
    }

    /**
     * Associates the specified int list with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void putIntArray(String key, List<Integer> value) {
        this.entries.put(key, BSOIntArray.of(value));
    }
    
    /**
     * Associates the specified long array with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void put(String key, long[] value) {
        this.entries.put(key, BSOLongArray.of(value));
    }

    /**
     * Associates the specified long list with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void putLongArray(String key, List<Long> value) {
        this.entries.put(key, BSOLongArray.of(value));
    }

    /**
     * Associates the specified float array with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void put(String key, float[] value) {
        this.entries.put(key, BSOFloatArray.of(value));
    }

    /**
     * Associates the specified float list with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void putFloatArray(String key, List<Float> value) {
        this.entries.put(key, BSOFloatArray.of(value));
    }

    /**
     * Associates the specified double array with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void put(String key, double[] value) {
        this.entries.put(key, BSODoubleArray.of(value));
    }

    /**
     * Associates the specified double list with the specified key in this map.
     * @param key The key with which the specified value is to be associated
     * @param value Value to be associated with the specified key
     */
    public void putDoubleArray(String key, List<Double> value) {
        this.entries.put(key, BSODoubleArray.of(value));
    }

    /**
     * Returns the byte to which the specified key is mapped, or 0 if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or {@code 0} if this map contains no mapping for the key
     */
    public byte getByte(String key) {
        if (this.containsKeyWithType(key, BSOTypes.BYTE)) return ((BSOByte)this.entries.get(key)).asByte();
        return 0;
    }
    
    /**
     * Returns the boolean to which the specified key is mapped, or false if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or {@code false} if this map contains no mapping for the key
     */
    public boolean getBoolean(String key) {
        return getByte(key) != 0;
    }

    /**
     * Returns the short to which the specified key is mapped, or 0 if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or {@code 0} if this map contains no mapping for the key
     */
    public short getShort(String key) {
        if (this.containsKeyWithType(key, BSOTypes.SHORT)) return ((BSOShort)this.entries.get(key)).asShort();
        return 0;
    }

    /**
     * Returns the int to which the specified key is mapped, or 0 if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or {@code 0} if this map contains no mapping for the key
     */
    public int getInt(String key) {
        if (this.containsKeyWithType(key, BSOTypes.INT)) return ((BSOInt)this.entries.get(key)).asInt();
        return 0;
    }

    /**
     * Returns the long to which the specified key is mapped, or 0 if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or {@code 0} if this map contains no mapping for the key
     */
    public long getLong(String key) {
        if (this.containsKeyWithType(key, BSOTypes.LONG)) return ((BSOLong)this.entries.get(key)).asLong();
        return 0;
    }

    /**
     * Returns the float to which the specified key is mapped, or 0.0f if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or {@code 0.0f} if this map contains no mapping for the key
     */
    public float getFloat(String key) {
        if (this.containsKeyWithType(key, BSOTypes.FLOAT)) return ((BSOFloat)this.entries.get(key)).asFloat();
        return 0.0f;
    }

    /**
     * Returns the double to which the specified key is mapped, or 0.0d if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or {@code 0.0d} if this map contains no mapping for the key
     */
    public double getDouble(String key) {
        if (this.containsKeyWithType(key, BSOTypes.DOUBLE)) return ((BSODouble)this.entries.get(key)).asDouble();
        return 0.0d;
    }

    /**
     * Returns the string to which the specified key is mapped, or an empty string if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or an empty string if this map contains no mapping for the key
     */
    public String getString(String key) {
        if (this.containsKeyWithType(key, BSOTypes.STRING)) return ((BSOString)this.entries.get(key)).getValue();
        return "";
    }

    /**
     * Returns the byte array to which the specified key is mapped, or an empty byte array if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or an empty byte array if this map contains no mapping for the key
     */
    public byte[] getByteArray(String key) {
        if (this.containsKeyWithType(key, BSOTypes.BYTE_ARRAY)) return ((BSOByteArray)this.entries.get(key)).getByteArray();
        return new byte[0];
    }

    /**
     * Returns the short array to which the specified key is mapped, or an empty short array if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or an empty short array if this map contains no mapping for the key
     */
    public short[] getShortArray(String key) {
        if (this.containsKeyWithType(key, BSOTypes.SHORT_ARRAY)) return ((BSOShortArray)this.entries.get(key)).getShortArray();
        return new short[0];
    }

    /**
     * Returns the int array to which the specified key is mapped, or an empty int array if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or an empty int array if this map contains no mapping for the key
     */
    public int[] getIntArray(String key) {
        if (this.containsKeyWithType(key, BSOTypes.INT_ARRAY)) return ((BSOIntArray)this.entries.get(key)).getIntArray();
        return new int[0];
    }

    /**
     * Returns the long array to which the specified key is mapped, or an empty long array if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or an empty long array if this map contains no mapping for the key
     */
    public long[] getLongArray(String key) {
        if (this.containsKeyWithType(key, BSOTypes.LONG_ARRAY)) return ((BSOLongArray)this.entries.get(key)).getLongArray();
        return new long[0];
    }

    /**
     * Returns the float array to which the specified key is mapped, or an empty float array if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or an empty float array if this map contains no mapping for the key
     */
    public float[] getFloatArray(String key) {
        if (this.containsKeyWithType(key, BSOTypes.FLOAT_ARRAY)) return ((BSOFloatArray)this.entries.get(key)).getFloatArray();
        return new float[0];
    }

    /**
     * Returns the double array to which the specified key is mapped, or an empty double array if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or an empty double array if this map contains no mapping for the key
     */
    public double[] getDoubleArray(String key) {
        if (this.containsKeyWithType(key, BSOTypes.DOUBLE_ARRAY)) return ((BSODoubleArray)this.entries.get(key)).getDoubleArray();
        return new double[0];
    }

    /**
     * Returns the {@link BSOMap} to which the specified key is mapped, or an empty {@link BSOMap} if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or an empty {@link BSOMap} if this map contains no mapping for the key
     */
    public BSOMap getMap(String key) {
        if (this.containsKeyWithType(key, BSOTypes.MAP)) return (BSOMap)this.entries.get(key);
        return new BSOMap();
    }

    /**
     * Returns the {@link BSOList} to which the specified key is mapped, or an empty {@link BSOList} if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or an empty {@link BSOList} if this map contains no mapping for the key
     */
    public BSOList getList(String key) {
        if (this.containsKeyWithType(key, BSOTypes.LIST)) return (BSOList)this.entries.get(key);
        return new BSOList();
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
     * @param key The key whose associated value is to be returned
     * @return The value to which the specified key is mapped, or null if this map contains no mapping for the key
     */
    @Nullable
    public BSOElement get(String key) {
        return this.entries.get(key);
    }

    /**
     * Removes the mapping for a key from this map if it is present.
     * @param key The key whose mapping is to be removed from the map
     * @return The previous value associated with key, or null if there was no mapping for key
     */
    @Nullable
    public BSOElement remove(String key) {
        return this.entries.remove(key);
    }

    /**
     * Removes all the entries from map.
     */
    public void clear() {
        this.entries.clear();
    }
    
    /**
     * Returns true if this list contains no elements.
     * @return {@code true} if this list contains no elements
     */
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
