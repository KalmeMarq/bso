package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import me.kalmemarq.bso.number.BSOByte;
import me.kalmemarq.bso.number.BSODouble;
import me.kalmemarq.bso.number.BSOFloat;
import me.kalmemarq.bso.number.BSOInt;
import me.kalmemarq.bso.number.BSOLong;
import me.kalmemarq.bso.number.BSOShort;

public class BSOList extends AbstractBSOList<BSOElement> implements Iterable<BSOElement> {
    protected final List<BSOElement> values;
    protected byte type = BSOElement.NULL_TYPE_ID;

    public BSOList() {
        this(new ArrayList<>());
    }

    public BSOList(List<BSOElement> list) {
        this.values = list;
    }

    @Override
    public BSOType<BSOList> getType() {
        return BSOTypes.LIST;
    }

    public List<BSOElement> getValues() {
        return values;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        this.checkType();

        if (!indefiniteLength) BSOUtil.writeLength(output, this.values.size());
        
        if (this.values.size() > 0 || indefiniteLength) output.writeByte(this.type);

        for (BSOElement el : this.values) {
            if (this.type == BSOElement.NULL_TYPE_ID) {
                output.write(el.getTypeId() + el.getAdditionalData());
            }
            el.write(output);
        }

        if (indefiniteLength) output.writeByte(END_TYPE_ID);
    }

    @Override
    public int getAdditionalData() {
        this.checkType();
        return super.getAdditionalData() + (this.type == BSOElement.NULL_TYPE_ID && !indefiniteLength && this.values.size() > 0 ? 0x40 : 0x00);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitList(this);
    }

    @Override
    public BSOList copy() {
        List<BSOElement> list = new ArrayList<>();
        for (BSOElement el : this.values) list.add(el.copy());
        return new BSOList(list);
    }

    @Override
    public Iterator<BSOElement> iterator() {
        return this.values.iterator();
    }

    @Override
    public int size() {
        return this.values.size();
    }

    @Override
    public BSOElement set(int index, BSOElement value) {
        return this.values.set(index, value);
    }

    @Override
    public void add(int index, BSOElement element) {
        this.values.add(index, element);
    }

    @Override
    public void add(BSOElement element) {
        this.values.add(element);
    }

    /**
     * Appends byte at the end of the list.
     */
    public void addByte(byte value) {
        this.values.add(BSOByte.of(value));
    }

    /**
     * Appends boolean at the end of the list.
     */
    public void addBoolean(boolean value) {
        this.values.add(BSOByte.of(value));
    }

    /**
     * Appends short at the end of the list.
     */
    public void addShort(short value) {
        this.values.add(BSOShort.of(value));
    }

    /**
     * Appends int at the end of the list.
     */
    public void addInt(int value) {
        this.values.add(BSOInt.of(value));
    }

    /**
     * Appends long at the end of the list.
     */
    public void addLong(long value) {
        this.values.add(BSOLong.of(value));
    }

    /**
     * Appends float at the end of the list.
     */
    public void addFloat(float value) {
        this.values.add(BSOFloat.of(value));
    }

    /**
     * Appends double at the end of the list.
     */
    public void addDouble(double value) {
        this.values.add(BSODouble.of(value));
    }

    /**
     * Appends String at the end of the list.
     */
    public void addString(String value) {
        this.values.add(BSOString.of(value));
    }

    /**
     * Appends Map at the end of the list.
     */
    public void addMap(Map<String, BSOElement> value) {
        this.values.add(BSOMap.of(value));
    }

    /**
     * Appends List at the end of the list.
     */
    public void addList(List<BSOElement> value) {
        this.values.add(new BSOList(value));
    }

    /**
     * Appends byte array at the end of the list.
     */
    public void addByteArray(byte[] value) {
        this.values.add(BSOByteArray.of(value));
    }

    /**
     * Appends byte list  at the end of the list.
     */
    public void addByteArray(List<Byte> value) {
        this.values.add(BSOByteArray.of(value));
    }

    /**
     * Appends short array at the end of the list.
     */
    public void addShortArray(short[] value) {
        this.values.add(BSOShortArray.of(value));
    }

    /**
     * Appends short list at the end of the list.
     */
    public void addShortArray(List<Short> value) {
        this.values.add(BSOShortArray.of(value));
    }

    /**
     * Appends int array at the end of the list.
     */
    public void addIntArray(int[] value) {
        this.values.add(BSOIntArray.of(value));
    }

    /**
     * Appends int list at the end of the list.
     */
    public void addIntArray(List<Integer> value) {
        this.values.add(BSOIntArray.of(value));
    }

    /**
     * Appends long array at the end of the list.
     */
    public void addLongArray(long[] value) {
        this.values.add(BSOLongArray.of(value));
    }

    /**
     * Appends long list at the end of the list.
     */
    public void addLongArray(List<Long> value) {
        this.values.add(BSOLongArray.of(value));
    }

    /**
     * Appends float array at the end of the list.
     */
    public void addFloatArray(float[] value) {
        this.values.add(BSOFloatArray.of(value));
    }

    /**
     * Appends float list at the end of the list.
     */
    public void addFloatArray(List<Float> value) {
        this.values.add(BSOFloatArray.of(value));
    }

    /**
     * Appends double array at the end of the list.
     */
    public void addDoubleArray(double[] value) {
        this.values.add(BSODoubleArray.of(value));
    }

    /**
     * Appends double list at the end of the list.
     */
    public void addDoubleArray(List<Double> value) {
        this.values.add(BSODoubleArray.of(value));
    }

    @Override
    public BSOElement remove(int index) {
        BSOElement b = this.values.remove(index);
        this.checkType();
        return b;
    }

    /**
     * Returns {@code true} if list contains the given element.
     * @param element Element whose presence in this list is to be tested
     * @return {@code true} if this list contains the specified element
     */
    public boolean contains(BSOElement element) {
        return this.values.contains(element);
    }

    /**
     * Sort this list according to the order induced by the given comparator.
     */
    public void sort(Comparator<? super BSOElement> comparator) {
        this.values.sort(comparator);
    }

    @Override
    public BSOElement get(int index) {
        return this.values.get(index);
    }

    private boolean isType(int index, BSOType<?> type) {
        return this.values.get(index).getType() == type;
    }

    /**
     * Returns the byte value at the specified position. If the index is out of bounds or the type at index isn't {@link BSOByte} it will return zero.
     * @param index The index of the value to return
     * @return Byte value, if present and type {@link BSOByte}. Otherwise, it will return {@code 0}
     */
    public byte getByte(int index) {
        if (index >= 0 && index < this.values.size() && this.isType(index, BSOTypes.BYTE)) {
            return ((BSOByte)this.values.get(index)).asByte();
        }
        return 0;
    }

    /**
     * Returns the short value at the specified position. If the index is out of bounds or the type at index isn't {@link BSOShort} it will return zero.
     * @param index The index of the value to return
     * @return Short value, if present and type {@link BSOShort}. Otherwise, it will return {@code 0}
     */
    public short getShort(int index) {
        if (index >= 0 && index < this.values.size() && this.isType(index, BSOTypes.SHORT)) {
            return ((BSOShort)this.values.get(index)).asShort();
        }
        return 0;
    }

    /**
     * Returns the int value at the specified position. If the index is out of bounds or the type at index isn't {@link BSOInt} it will return zero.
     * @param index The index of the value to return
     * @return Int value, if present and type {@link BSOInt}. Otherwise, it will return {@code 0}
     */
    public int getInt(int index) {
        if (index >= 0 && index < this.values.size() && this.isType(index, BSOTypes.INT)) {
            return ((BSOInt)this.values.get(index)).asInt();
        }
        return 0;
    }

    /**
     * Returns the long value at the specified position. If the index is out of bounds or the type at index isn't {@link BSOLong} it will return zero.
     * @param index The index of the value to return
     * @return long value, if present and type {@link BSOLong}. Otherwise, it will return {@code 0}
     */
    public long getLong(int index) {
        if (index >= 0 && index < this.values.size() && this.isType(index, BSOTypes.LONG)) {
            return ((BSOLong)this.values.get(index)).asLong();
        }
        return 0L;
    }

    /**
     * Returns the float value at the specified position. If the index is out of bounds or the type at index isn't {@link BSOFloat} it will return zero.
     * @param index The index of the value to return
     * @return Float value, if present and type {@link BSOFloat}. Otherwise, it will return {@code 0.0f}
     */
    public float getFloat(int index) {
        if (index >= 0 && index < this.values.size() && this.isType(index, BSOTypes.FLOAT)) {
            return ((BSOFloat)this.values.get(index)).asFloat();
        }
        return 0.0f;
    }

    /**
     * Returns the double value at the specified position. If the index is out of bounds or the type at index isn't {@link BSODouble} it will return zero.
     * @param index The index of the value to return
     * @return Double value, if present and type {@link BSODouble}. Otherwise, it will return {@code 0.0d}
     */
    public double getDouble(int index) {
        if (index >= 0 && index < this.values.size() && this.isType(index, BSOTypes.DOUBLE)) {
            return ((BSODouble)this.values.get(index)).asDouble();
        }
        return 0.0d;
    }

    /**
     * Returns the String at the specified position. If the index is out of bounds or the type at index isn't {@link BSOString} it will return an empty string.
     * @param index The index of the value to return
     * @return String, if present and type {@link BSOString}. Otherwise, it will return {@code ""}
     */
    public String getString(int index) {
        if (index >= 0 && index < this.values.size() && this.isType(index, BSOTypes.STRING)) {
            return ((BSOString)this.values.get(index)).getValue();
        }
        return "";
    }

    /**
     * Returns the {@link BSOMap} at the specified position. If the index is out of bounds or the type at index isn't {@link BSOMap} it will return an empty {@link BSOMap}.
     * @param index The index of the value to return
     * @return {@link BSOMap}, if present and type {@link BSOMap}. Otherwise, it will return an empty {@link BSOMap}
     */
    public BSOMap getMap(int index) {
        if (index >= 0 && index < this.values.size() && this.isType(index, BSOTypes.MAP)) {
            return (BSOMap)this.values.get(index);
        }
        return new BSOMap();
    }

    /**
     * Returns the {@link BSOList} at the specified position. If the index is out of bounds or the type at index isn't {@link BSOList} it will return an empty {@link BSOList}.
     * @param index The index of the value to return
     * @return {@link BSOList}, if present and type {@link BSOList}. Otherwise, it will return an empty {@link BSOList}
     */
    public BSOList getList(int index) {
        if (index >= 0 && index < this.values.size() && this.isType(index, BSOTypes.LIST)) {
            return (BSOList)this.values.get(index);
        }
        return new BSOList();
    }

    /**
     * Returns the byte array at the specified position. If the index is out of bounds or the type at index isn't BSOByteArray it will return an empty byte array.
     * @param index The index of the value to return
     * @return {@code byte[]}, if present and type BSOByteArray. Otherwise, it will return an empty {@code byte[]}
     */
    public byte[] getByteArray(int index) {
        if (index >= 0 && index < this.values.size() && this.isType(index, BSOTypes.BYTE_ARRAY)) {
            return ((BSOByteArray)this.values.get(index)).getByteArray();
        }
        return new byte[0];
    }

    /**
     * Returns the short array at the specified position. If the index is out of bounds or the type at index isn't BSOShortArray it will return an empty short array.
     * @param index The index of the value to return
     * @return {@code short[]}, if present and type BSOShortArray. Otherwise, it will return an empty {@code short[]}
     */
    public short[] getShortArray(int index) {
        if (index >= 0 && index < this.values.size() && this.isType(index, BSOTypes.SHORT_ARRAY)) {
            return ((BSOShortArray)this.values.get(index)).getShortArray();
        }
        return new short[0];
    }

    /**
     * Returns the int array at the specified position. If the index is out of bounds or the type at index isn't BSOIntArray it will return an empty int array.
     * @param index The index of the value to return
     * @return {@code int[]}, if present and type BSOIntArray. Otherwise, it will return an empty {@code int[]}
     */
    public int[] getIntArray(int index) {
        if (index >= 0 && index < this.values.size() && this.isType(index, BSOTypes.INT_ARRAY)) {
            return ((BSOIntArray)this.values.get(index)).getIntArray();
        }
        return new int[0];
    }

    /**
     * Returns the long array at the specified position. If the index is out of bounds or the type at index isn't BSOLongArray it will return an empty long array.
     * @param index The index of the value to return
     * @return {@code long[]}, if present and type BSOLongArray. Otherwise, it will return an empty {@code long[]}
     */
    public long[] getLongArray(int index) {
        if (index >= 0 && index < this.values.size() && this.isType(index, BSOTypes.LONG_ARRAY)) {
            return ((BSOLongArray)this.values.get(index)).getLongArray();
        }
        return new long[0];
    }

    /**
     * Returns the float array at the specified position. If the index is out of bounds or the type at index isn't BSOIntArray it will return an empty int array.
     * @param index The index of the value to return
     * @return {@code float[]}, if present and type BSOFloatArray. Otherwise, it will return an empty {@code float[]}
     */
    public float[] getFloatArray(int index) {
        if (index >= 0 && index < this.values.size() && this.isType(index, BSOTypes.FLOAT_ARRAY)) {
            return ((BSOFloatArray)this.values.get(index)).getFloatArray();
        }
        return new float[0];
    }

    /**
     * Returns the double array at the specified position. If the index is out of bounds or the type at index isn't BSODoubleArray it will return an empty double array.
     * @param index The index of the value to return
     * @return {@code double[]}, if present and type BSODoubleArray. Otherwise, it will return an empty {@code int[]}
     */
    public double[] getDoubleArray(int index) {
        if (index >= 0 && index < this.values.size() && this.isType(index, BSOTypes.DOUBLE_ARRAY)) {
            return ((BSODoubleArray)this.values.get(index)).getDoubleArray();
        }
        return new double[0];
    }

    @Override
    public void clear() {
        this.values.clear();
        this.checkType();
    }

    /**
     * Returns the index of the first occurrence of the specified element in this list, or -1 if this list does not contain the element.
     * @return The index of the first occurrence of the specified element in this list, or -1 if this list does not contain the element
     */
    public int indexOf(BSOElement element) {
        return this.values.indexOf(element);
    }

    private void checkType() {
        if (this.values.isEmpty()) this.type = BSOElement.NULL_TYPE_ID;
        else {
            this.type = BSOElement.NULL_TYPE_ID;
            for (int i = 0; i < this.values.size(); i++) {
                BSOElement el = this.values.get(i);

                if (this.type == BSOElement.NULL_TYPE_ID) {
                    this.type = el.getTypeId();
                } else if (this.type != el.getTypeId()) {
                    this.type = BSOElement.NULL_TYPE_ID;
                    break;
                }
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BSOByteArray)) return false;
        return Objects.equals(((BSOList)obj).values, this.values);
    }

    @Override
    public int hashCode() {
        return this.values.hashCode();
    }

    @Override
    public String toString() {
        return this.asString();
    }
}