package io.github.kalmemarq.bso;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public record BsoCustom<T>(BsoCustomType<T> type, T value) implements BsoNode {
    public BsoCustom(T value) {
        this(BsoContext.global(), value);
    }

    @SuppressWarnings("unchecked")
    public BsoCustom(BsoContext context, T value) {
        this((BsoCustomType<T>) typeFor(context, value.getClass()), value);
    }

    private static BsoCustomType<?> typeFor(BsoContext context, Class<?> clazz) {
        BsoCustomType<?> customType = context.byClass(clazz);
        if (customType == null) throw new IllegalArgumentException("There's no custom type for class '" + clazz.getName() + "'");
        return customType;
    }

    @Override
    public BsoNode copy() {
        return this.type.copy(this);
    }

    public interface BsoCustomType<T> {
        Class<T> getClazz();

        int getId();

        default int getAd(BsoCustom<T> node) {
            return 0;
        }

        BsoCustom<T> read(DataInput in, int ad) throws IOException;
        void write(DataOutput out, BsoCustom<T> node) throws IOException;

        String getName();
        BsoCustom<T> parse(SBsoReader reader) throws IOException;
        String write(BsoCustom<T> node);

        default BsoCustom<T> copy(BsoCustom<T> src) {
            return src;
        }
    }
}
