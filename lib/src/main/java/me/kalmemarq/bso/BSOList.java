package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BSOList extends AbstractBSOList<BSOElement> {
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

    @Override
    public byte getHeldTypeId() {
        return this.type;
    }

    public List<BSOElement> getValues() {
        return values;
    }

    @Override
    public void write(DataOutput output) throws IOException {
        this.checkType();

        if (!indefiniteLength) BSOUtils.writeLength(output, this.values.size());
        
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

    public boolean addElement(BSOElement element) {
        return this.values.add(element);
    }

    @Override
    public BSOElement remove(int index) {
        BSOElement b = this.values.remove(index);
        this.checkType();
        return b;
    }

    @Override
    public BSOElement get(int index) {
        return this.values.get(index);
    }

    @Override
    public void clear() {
        this.values.clear();
        this.checkType();
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