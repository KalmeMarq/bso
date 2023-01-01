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
    private final Map<String, BSOElement> entries;
    private boolean indefiniteLength = true;

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

    public boolean contains(String key) {
        return this.entries.containsKey(key);
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

    public void put(String key, boolean[] value) {
        this.entries.put(key, BSOByteArray.of(value));
    }

    public void putByteArray(String key, List<Byte> value) {
        this.entries.put(key, BSOByteArray.of(value));
    }

    public void put(String key, short[] value) {
        this.entries.put(key, BSOShortArray.of(value));
    }

    public void putShortArray(String key, List<Short> value) {
        this.entries.put(key, BSOShortArray.of(value));
    }
    
    public void put(String key, int[] value) {
        this.entries.put(key, BSOIntArray.of(value));
    }

    public void putIntArray(String key, List<Integer> value) {
        this.entries.put(key, BSOIntArray.of(value));
    }
    
    public void put(String key, long[] value) {
        this.entries.put(key, BSOLongArray.of(value));
    }

    public void putLongArray(String key, List<Long> value) {
        this.entries.put(key, BSOLongArray.of(value));
    }

    public void put(String key, float[] value) {
        this.entries.put(key, BSOFloatArray.of(value));
    }

    public void putFloatArray(String key, List<Float> value) {
        this.entries.put(key, BSOFloatArray.of(value));
    }

    public void put(String key, double[] value) {
        this.entries.put(key, BSODoubleArray.of(value));
    }

    public void putDoubleArray(String key, List<Double> value) {
        this.entries.put(key, BSODoubleArray.of(value));
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