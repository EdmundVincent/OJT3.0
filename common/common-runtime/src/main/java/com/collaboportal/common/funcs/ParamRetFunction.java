package com.collaboportal.common.funcs;



@FunctionalInterface
public interface ParamRetFunction<T, R> {

    R run(T param);

}
