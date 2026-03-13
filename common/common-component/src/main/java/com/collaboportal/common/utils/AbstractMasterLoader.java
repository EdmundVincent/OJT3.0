package com.collaboportal.common.utils;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import com.collaboportal.common.funcs.MasterLoader;

/**
 * マスターデータをロードするための抽象クラス
 * 
 * @param <T> ロードするデータの型
 */
public abstract class AbstractMasterLoader<T> implements MasterLoader<T> {

    // メールアドレスからデータをロードするための関数
    private final Function<String, T> loaderFunction;

    // データが存在しない場合のフォールバック値を提供するサプライヤ
    private final Supplier<T> fallbackSupplier;

    /**
     * コンストラクタ
     * 
     * @param loaderFunction   データロード用関数
     * @param fallbackSupplier フォールバック用サプライヤ
     */
    protected AbstractMasterLoader(Function<String, T> loaderFunction, Supplier<T> fallbackSupplier) {
        this.loaderFunction = loaderFunction;
        this.fallbackSupplier = fallbackSupplier;
    }

    /**
     * メールアドレスを基にデータをロードする
     * 
     * @param email メールアドレス
     * @return ロードされたデータ。存在しない場合はフォールバック値
     */
    @Override
    public T loadByKey(String email) {
        return Optional.ofNullable(loaderFunction.apply(email)).orElseGet(fallbackSupplier);
    }

}
