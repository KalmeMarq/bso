package io.github.kalmemarq.bso;

import java.util.*;

public final class BsoList implements BsoNode {
    private final List<BsoNode> list;

    public BsoList() {
        this(new ArrayList<>());
    }

    public BsoList(List<BsoNode> list) {
        this.list = list;
    }

    @Override
    public Iterator<BsoNode> iterator() {
        return this.list.iterator();
    }

    @Override
    public BsoNode get(int index) {
        return this.list.get(index);
    }

    @Override
    public boolean has(int index) {
        return index >= 0 && index < this.list.size();
    }

    public BsoNode remove(int index) {
        return this.list.remove(index);
    }

    public void add(BsoNode node) {
        this.list.add(node);
    }

    public void addByte(int value) {
        this.list.add(new BsoByte((byte) value));
    }

    public void addUByte(int value) {
        this.list.add(new BsoUByte((byte) (value & 0xFF)));
    }

    public void addShort(int value) {
        this.list.add(new BsoShort((short) value));
    }

    public void addUShort(int value) {
        this.list.add(new BsoShort((short) (value & 0xFFFF)));
    }

    public void addInt(int value) {
        this.list.add(new BsoInt(value));
    }

    public void addUInt(long value) {
        this.list.add(new BsoUInt((int) (value & 0xFFFFFFFFL)));
    }

    public void addLong(long value) {
        this.list.add(new BsoLong(value));
    }

    public void addULong(long value) {
        this.list.add(new BsoLong(value));
    }

    public void addFloat(float value) {
        this.list.add(new BsoFloat(value));
    }

    public void addDouble(double value) {
        this.list.add(new BsoDouble(value));
    }

    public void addString(String value) {
        this.list.add(new BsoString(value));
    }

    @Override
    public int size() {
        return this.list.size();
    }

    public void clear() {
        this.list.clear();
    }

    @Override
    public BsoNode copy() {
        List<BsoNode> bso = new ArrayList<>(this.list.size());
        for (BsoNode entry : this.list) {
            bso.add(entry.copy());
        }
        return new BsoList(bso);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return obj instanceof BsoList n && Objects.equals(this.list, n.list);
    }

    @Override
    public int hashCode() {
        return this.list.hashCode();
    }
}
