package me.kalmemarq.bso;

import java.util.AbstractList;

public abstract class AbstractBSOList<T extends BSOElement> extends AbstractList<T> implements BSOElement {
    protected boolean indefiniteLength = false;
    
    public void setIndefiniteLength(boolean use) {
        this.indefiniteLength = use;
    }

    @Override
    public abstract T set(int index, T value);

    @Override
    public abstract void add(int index, T value);

    @Override
    public abstract T remove(int index);

    public abstract byte getHeldTypeId();

    @Override
    public int getAdditionalData() {
        return BSOUtils.lengthAdditionalData(this.size(), this.indefiniteLength);
    }
}
