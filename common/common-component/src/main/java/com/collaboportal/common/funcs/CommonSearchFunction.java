package com.collaboportal.common.funcs;

/**
 * Collaboportalアプリケーションの共通検索機能を提供するインターフェース
 */
public interface CommonSearchFunction {

    /**
     * キーによるオブジェクト検索のための関数型インターフェース
     * 
     * @param <T> 検索対象のオブジェクトの型
     */
    @FunctionalInterface
    public interface Finder<T> {
        /**
         * 指定されたキーでオブジェクトを検索します
         * 
         * @param key 検索キー
         * @return 検索されたT型のオブジェクト
         */
        T find(String key);
    }

    public interface ErrorResolver<T> {

        T resolve(T res);
    
        
    }

    public interface SearchExecutor {
        /**
         * 検索を実行し、結果を返す
         * 
         * @param <T> 検索結果の型
         * @param searchKey 検索キー
         * @param finder 検索処理
         * @param resolver エラー処理
         * @return 検索結果（エラー発生時は代替値）
         */
        default <T> T executeSearch(String searchKey, Finder<T> finder, ErrorResolver<T> resolver) {
            // 検索処理を実行
            T result = finder.find(searchKey);
            // resolverの処理を通した検索結果を返す
            return resolver.resolve(result);
        }
    }


}
