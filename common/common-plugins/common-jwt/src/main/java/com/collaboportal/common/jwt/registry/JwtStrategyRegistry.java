package com.collaboportal.common.jwt.registry;

import com.collaboportal.common.jwt.strategy.JwtClaimResolver;
import com.collaboportal.common.jwt.strategy.JwtTokenGenerator;
import com.collaboportal.common.jwt.strategy.JwtTokenValidator;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * JWTストラテジーレジストリ
 * 各種JWT関連のストラテジーインスタンスを管理・保存するために使用される：
 * - クレームリゾルバー（ClaimResolver）：JWT ClaimsFromSpecificFieldValue抽出
 * - トークンジェネレーター（TokenGenerator）：入力データに基づくJWTトークン生成
 * - トークンバリデーター（TokenValidator）：JWTトークンの有効性検証
 */
@Component
public class JwtStrategyRegistry {

    /** クレームリゾルバーのマッピングテーブル、keyはリゾルバー名、valueはリゾルバーインスタンス */
    private final Map<String, JwtClaimResolver<?>> claimResolvers = new HashMap<>();

    /** トークンジェネレーターのマッピングテーブル、keyはジェネレーター名、valueはジェネレーターインスタンス */
    private final Map<String, JwtTokenGenerator<?>> tokenGenerators = new HashMap<>();

    /** トークンバリデーターのマッピングテーブル、keyはバリデーター名、valueはバリデーターインスタンス */
    private final Map<String, JwtTokenValidator> tokenValidators = new HashMap<>();

    /**
     * クレームリゾルバーを登録する
     * 
     * @param key      リゾルバーの一意識別名
     * @param resolver リゾルバーインスタンス、JWT Claimsから特定の型のデータを抽出するために使用
     * @param <T>      リゾルバー戻り値の型
     */
    public <T> void registerClaimResolver(String key, JwtClaimResolver<T> resolver) {
        claimResolvers.put(key, resolver);
    }

    /**
     * クレームリゾルバーを取得する
     * 
     * @param key リゾルバーの一意識別名
     * @return 対応するリゾルバーインスタンス、存在しない場合はnullを返す
     * @param <T> リゾルバー戻り値の型
     */
    public <T> JwtClaimResolver<T> getClaimResolver(String key) {
        return (JwtClaimResolver<T>) claimResolvers.get(key);
    }

    /**
     * トークンジェネレーターを登録する
     * 
     * @param key       ジェネレーターの一意識別名
     * @param generator ジェネレーターインスタンス、入力データに基づいてJWTトークンを生成するために使用
     * @param <T>       ジェネレーター入力パラメータの型
     */
    public <T> void registerTokenGenerator(String key, JwtTokenGenerator<T> generator) {
        tokenGenerators.put(key, generator);
    }

    /**
     * トークンジェネレーターを取得する
     * 
     * @param key ジェネレーターの一意識別名
     * @return 対応するジェネレーターインスタンス、存在しない場合はnullを返す
     * @param <T> ジェネレーター入力パラメータの型
     */
    public <T> JwtTokenGenerator<T> getTokenGenerator(String key) {
        return (JwtTokenGenerator<T>) tokenGenerators.get(key);
    }

    /**
     * トークンバリデーターを登録する
     * 
     * @param key       バリデーターの一意識別名
     * @param validator バリデーターインスタンス、JWTトークンの有効性を検証するために使用
     */
    public void registerTokenValidator(String key, JwtTokenValidator validator) {
        tokenValidators.put(key, validator);
    }

    /**
     * トークンバリデーターを取得する
     * 
     * @param key バリデーターの一意識別名
     * @return 対応するバリデーターインスタンス、存在しない場合はnullを返す
     */
    public JwtTokenValidator getTokenValidator(String key) {
        return tokenValidators.get(key);
    }
}
