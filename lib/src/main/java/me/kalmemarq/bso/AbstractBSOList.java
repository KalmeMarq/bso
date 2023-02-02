package me.kalmemarq.bso;

public abstract class AbstractBSOList<T extends BSOElement> implements BSOElement {
    protected boolean indefiniteLength = false;
    
    public void setIndefiniteLength(boolean use) {
        this.indefiniteLength = use;
    }

    /**
     * Replaces the element at the specified position in the list.
     * @param index The index of the element to be replaced
     * @param value Element to be stored at the specified position
     * @return The element previously at the specified position
     */
    public abstract T set(int index, T value);

    /**
     * Inserts the given element at the specified position in the list.
     * @param index The index at which the element will be inserted
     * @param value Element to be inserted
     */
    public abstract void add(int index, T value);

    /**
     * Appends the given element at the end of the list.
     * @param value Element to be appended
     */
    public abstract void add(T value);

    /**
     * Removes element at the specified position.
     * @param index The index of the element to be removed
     * @return The element previously at the specified position
     */
    public abstract T remove(int index);

    /**
     * Returns the number of elements in this list.
     * @return Number of elements in this list
     */
    public abstract int size();

    /**
     * Returns the element at the specified position in the list. If it's a number array it will return zero if index is out of bounds.
     * @param index The index of the element to return
     * @return The element at the specified position
     * @throws IndexOutOfBoundsException  if the index is out of range
     */
    public abstract T get(int index);

    /**
     * Removes all the elements from list.
     */
    public abstract void clear();

    /**
     * Returns true if this list contains no elements.
     * @return {@code true} if this list contains no elements
     */
    public boolean isEmpty() {
        return this.size() <= 0;
    }

    @Override
    public int getAdditionalData() {
        if (this.size() <= Byte.MAX_VALUE * 2 + 1) {
            return BSOUtil.BYTE_LENGTH;
        } else if (this.size() <= Short.MAX_VALUE * 2 + 1) {
            return BSOUtil.SHORT_LENGTH;
        }
        return BSOUtil.INT_LENGTH;
    }
}
