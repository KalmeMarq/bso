package io.github.kalmemarq.bso;

import io.github.kalmemarq.bso.BsoCustom.BsoCustomType;

import java.util.HashMap;
import java.util.Map;

public final class BsoContext {
    private static final BsoContext GLOBAL = new BsoContext();

    private final Map<Integer, BsoCustomType<?>> byId = new HashMap<>();
    private final Map<Class<?>, BsoCustomType<?>> byClazz = new HashMap<>();
    private final Map<String, BsoCustomType<?>> byName = new HashMap<>();
    private int maxLength = 1 << 24;

    public static BsoContext global() {
        return GLOBAL;
    }

    public int maxLength() {
        return this.maxLength;
    }

    public BsoContext maxLength(int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength must be non-negative");
        }
        this.maxLength = maxLength;
        return this;
    }

    public <T> BsoCustomType<T> register(BsoCustomType<T> type) {
        this.byId.put(type.getId(), type);
        this.byClazz.put(type.getClazz(), type);
        this.byName.put(type.getName(), type);
        return type;
    }

    public <T> BsoCustomType<T> unregister(BsoCustomType<T> type) {
        this.byId.remove(type.getId());
        this.byClazz.remove(type.getClazz());
        this.byName.remove(type.getName());
        return type;
    }

    public void unregisterAll() {
        this.byId.clear();
        this.byClazz.clear();
        this.byName.clear();
    }

    public BsoCustomType<?> byId(int id) {
        return this.byId.get(id);
    }

    public BsoCustomType<?> byClass(Class<?> clazz) {
        return this.byClazz.get(clazz);
    }

    public BsoCustomType<?> byName(String name) {
        return this.byName.get(name);
    }
}
