package com.collaboportal.common.funcs;

@FunctionalInterface
public interface ParamFunction<T> {

    void run(T param);

}
