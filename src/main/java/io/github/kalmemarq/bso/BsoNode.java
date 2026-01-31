package io.github.kalmemarq.bso;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

public sealed interface BsoNode extends Iterable<BsoNode> permits BsoByteArray, BsoDoubleArray, BsoFloatArray, BsoIntArray, BsoList, BsoLongArray, BsoMap, BsoMissing, BsoPrimitive, BsoShortArray, BsoUByteArray, BsoUIntArray, BsoULongArray, BsoUShortArray {
    @Override
    default Iterator<BsoNode> iterator() {
        return Collections.emptyIterator();
    }

    default Set<Entry<String, BsoNode>> properties() {
        return Collections.emptySet();
    }

    default BsoNode path(String name) {
        BsoNode value = this.get(name);
        return value == null ? BsoMissing.INSTANCE : value;
    }

    default BsoNode path(int index) {
        BsoNode value = this.get(index);
        return value == null ? BsoMissing.INSTANCE : value;
    }

    default BsoNode get(String name) {
        return null;
    }

    default BsoNode get(int index) {
        return null;
    }

    default boolean has(String name) {
        return false;
    }

    default boolean has(int index) {
        return false;
    }

    default void put(String key, BsoNode node) {
    }

    default int size() {
        return 0;
    }

    default boolean isEmpty() {
        return this.size() == 0;
    }

    BsoNode copy();

    //

    default Number asNumber() {
        return this.asNumber(0);
    }

    default Number asNumber(Number defaultValue) {
        return defaultValue;
    }

    default byte asByte() {
        return this.asByte(0);
    }

    default byte asByte(int defaultValue) {
        return this.asNumber(defaultValue).byteValue();
    }

    default short asShort() {
        return this.asShort(0);
    }

    default short asShort(int defaultValue) {
        return this.asNumber(defaultValue).shortValue();
    }

    default int asInt() {
        return this.asInt(0);
    }

    default int asInt(int defaultValue) {
        return this.asNumber(defaultValue).intValue();
    }

    default long asLong() {
        return this.asLong(0);
    }

    default long asLong(long defaultValue) {
        return this.asNumber(defaultValue).longValue();
    }

    default float asFloat() {
        return this.asFloat(0);
    }

    default float asFloat(float defaultValue) {
        return this.asNumber(defaultValue).floatValue();
    }

    default double asDouble() {
        return this.asDouble(0);
    }

    default double asDouble(double defaultValue) {
        return this.asNumber(defaultValue).doubleValue();
    }

    default boolean asBool() {
        return this.asBool(false);
    }

    default boolean asBool(boolean defaultValue) {
        return this.asNumber(defaultValue ? 0 : 1).byteValue() != 0;
    }

    default String asString() {
        return this.asString("");
    }

    default String asString(String defaultValue) {
        return defaultValue;
    }

    default byte[] asByteArray() {
        return this.asByteArray(new byte[0]);
    }

    default byte[] asByteArray(byte[] values) {
        return values;
    }

    default short[] asShortArray() {
        return this.asShortArray(new short[0]);
    }

    default short[] asShortArray(short[] values) {
        return values;
    }

    default int[] asIntArray() {
        return this.asIntArray(new int[0]);
    }

    default int[] asIntArray(int[] values) {
        return values;
    }

    default long[] asLongArray() {
        return this.asLongArray(new long[0]);
    }

    default long[] asLongArray(long[] values) {
        return values;
    }

    default float[] asFloatArray() {
        return this.asFloatArray(new float[0]);
    }

    default float[] asFloatArray(float[] values) {
        return values;
    }

    default double[] asDoubleArray() {
        return this.asDoubleArray(new double[0]);
    }

    default double[] asDoubleArray(double[] values) {
        return values;
    }
}
