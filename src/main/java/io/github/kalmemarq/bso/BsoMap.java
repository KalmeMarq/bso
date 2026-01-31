package io.github.kalmemarq.bso;

import java.util.*;
import java.util.Map.Entry;

public final class BsoMap implements BsoNode {
    private final Map<String, BsoNode> map;

    public BsoMap() {
        this(new HashMap<>());
    }

    public BsoMap(Map<String, BsoNode> map) {
        this.map = map;
    }

    @Override
    public Iterator<BsoNode> iterator() {
        return this.map.values().iterator();
    }

    @Override
    public Set<Entry<String, BsoNode>> properties() {
        return this.map.entrySet();
    }

    @Override
    public BsoNode get(String name) {
        return this.map.get(name);
    }

    @Override
    public boolean has(String name) {
        return this.map.containsKey(name);
    }

    public BsoNode remove(String name) {
        return this.map.remove(name);
    }

    @Override
    public void put(String name, BsoNode node) {
        this.map.put(name, node == null ? BsoMissing.INSTANCE : node);
    }

    public void putByte(String name, int value) {
        this.map.put(name, new BsoByte((byte) value));
    }

    public void putUByte(String name, int value) {
        this.map.put(name, new BsoUByte((byte) (value & 0xFF)));
    }

    public void putShort(String name, int value) {
        this.map.put(name, new BsoShort((short) value));
    }

    public void putUShort(String name, int value) {
        this.map.put(name, new BsoShort((short) (value & 0xFFFF)));
    }

    public void putInt(String name, int value) {
        this.map.put(name, new BsoInt(value));
    }

    public void putUInt(String name, long value) {
        this.map.put(name, new BsoUInt((int) (value & 0xFFFFFFFFL)));
    }

    public void putLong(String name, long value) {
        this.map.put(name, new BsoLong(value));
    }

    public void putULong(String name, long value) {
        this.map.put(name, new BsoLong(value));
    }

    public void putFloat(String name, float value) {
        this.map.put(name, new BsoFloat(value));
    }

    public void putDouble(String name, double value) {
        this.map.put(name, new BsoDouble(value));
    }

    public void putString(String name, String value) {
        this.map.put(name, new BsoString(value));
    }

    public void putByteArray(String name, byte[] values) {
        this.map.put(name, new BsoByteArray(values));
    }

    public void putUByteArray(String name, byte[] values) {
        this.map.put(name, new BsoUByteArray(values));
    }

    public void putShortArray(String name, short[] values) {
        this.map.put(name, new BsoShortArray(values));
    }

    public void putUShortArray(String name, short[] values) {
        this.map.put(name, new BsoShortArray(values));
    }

    public void putIntArray(String name, int[] values) {
        this.map.put(name, new BsoIntArray(values));
    }

    public void putUIntArray(String name, int[] values) {
        this.map.put(name, new BsoUIntArray(values));
    }

    public void putLongArray(String name, long[] values) {
        this.map.put(name, new BsoLongArray(values));
    }

    public void putULongArray(String name, long[] values) {
        this.map.put(name, new BsoULongArray(values));
    }

    public void putFloatArray(String name, float[] values) {
        this.map.put(name, new BsoFloatArray(values));
    }

    public void putDoubleArray(String name, double[] values) {
        this.map.put(name, new BsoDoubleArray(values));
    }

    @Override
    public int size() {
        return this.map.size();
    }

    public void clear() {
        this.map.clear();
    }

    @Override
    public BsoNode copy() {
        HashMap<String, BsoNode> map = new HashMap<>();
        this.map.forEach((key, value) -> map.put(key, value.copy()));
        return new BsoMap(map);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof BsoMap n && Objects.equals(this.map, n.map);
    }

    @Override
    public int hashCode() {
        return this.map.hashCode();
    }
}
