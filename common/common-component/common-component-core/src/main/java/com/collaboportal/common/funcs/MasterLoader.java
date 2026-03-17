package com.collaboportal.common.funcs;

@FunctionalInterface
public interface MasterLoader<T> {
    T loadByKey(String key);
}
