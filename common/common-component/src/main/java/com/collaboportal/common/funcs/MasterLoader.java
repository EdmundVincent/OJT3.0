package com.collaboportal.common.funcs;

/**
 * マスターデータローダーインターフェース
 * メールアドレスをキーとしてデータをロードする機能を提供する
 * 
 * @param <T> ロードするデータの型
 */
@FunctionalInterface
public interface MasterLoader<T> {
    /**
     * 指定されたメールアドレスでデータをロードする
     * 
     * @param key メールアドレス
     * @return ロードされたデータ
     */
    T loadByKey(String key);
}
