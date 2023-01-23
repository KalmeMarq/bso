package me.kalmemarq.bso;

import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BSOList extends AbstractBSOList<BSOElement> {
    private final List<BSOElement> values;
    private byte type = BSOTypes.NULL.getId();

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
        this.type = this.values.isEmpty() ? BSOTypes.NULL.getId(): this.values.get(0).getTypeId();

        output.writeByte(this.type);
        if (!indefiniteLength) BSOUtils.writeLength(output, this.values.size());

        for (BSOElement el : this.values) {
            el.write(output);
        }

        if (indefiniteLength) output.writeByte(END_TYPE_ID);
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
        if (this.values.isEmpty()) this.type = 0;
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