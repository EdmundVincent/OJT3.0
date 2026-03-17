package com.collaboportal.common.utils;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import com.collaboportal.common.funcs.MasterLoader;

public abstract class AbstractMasterLoader<T> implements MasterLoader<T> {
    private final Function<String, T> loaderFunction;
    private final Supplier<T> fallbackSupplier;
    protected AbstractMasterLoader(Function<String, T> loaderFunction, Supplier<T> fallbackSupplier) {
        this.loaderFunction = loaderFunction;
        this.fallbackSupplier = fallbackSupplier;
    }
    @Override
    public T loadByKey(String email) {
        return Optional.ofNullable(loaderFunction.apply(email)).orElseGet(fallbackSupplier);
    }
}
