package com.collaboportal.common.oauth2.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.oauth2.strategy.auth.JwtAuthStrategy;



public class JwtTokenStrategyRegistry {

    Logger logger = LoggerFactory.getLogger(JwtTokenStrategyRegistry.class);

    public JwtTokenStrategyRegistry() {
        logger.debug("JwtTokenStrategyRegistryの初期化が完了しました");
    }

    // ストラテジーを保持するマップ
    // キー: ストラテジー識別キー, 値: トークン解決関数
    private final Map<String, JwtAuthStrategy> strategyMap = new ConcurrentHashMap<>();

    public void register(String key, JwtAuthStrategy strategy) {
        logger.debug("[JWTストラテジー登録] 新しいストラテジーを登録します。キー: {}", key);
        strategyMap.put(key, strategy);
        logger.info("[JWTストラテジー登録] ストラテジーの登録が正常に完了しました。キー: {}", key);
    }

    /**
     * 指定されたキーに対応するストラテジーを取得する
     * 
     * @param key 取得するストラテジーのキー
     * @return 対応するトークン解決関数
     */
    public JwtAuthStrategy getStrategy(String key) {
        logger.debug("[JWTストラテジー取得] ストラテジーの取得を開始します。キー: {}", key);
        JwtAuthStrategy strategy = strategyMap.get(key);
        if (strategy == null) {
            logger.warn("[JWTストラテジー取得] 指定されたキーに対応するストラテジーが見つかりませんでした。キー: {}", key);
        } else {
            logger.debug("[JWTストラテジー取得] ストラテジーの取得に成功しました。キー: {}", key);
        }
        return strategy;
    }

    /**
     * 指定されたストラテジーを使用してトークンを解決する
     * 
     * @param request     HTTPリクエスト
     * @param strategyKey 使用するストラテジーのキー
     * @return 解決されたトークン。ストラテジーが存在しない場合はnull
     */
    public String resolveToken(BaseRequest request, String strategyKey) {
        logger.debug("[トークン解決] トークン解決処理を開始します。ストラテジーキー: {}", strategyKey);
        JwtAuthStrategy strategy = strategyMap.get(strategyKey);
        if (strategy == null) {
            logger.error("[トークン解決] 指定されたストラテジーが見つかりませんでした。キー: {}", strategyKey);
            return null;
        }

        String token = strategy.execute(request);
        if (token == null) {
            logger.warn("[トークン解決] トークンの解決に失敗しました。ストラテジーキー: {}", strategyKey);
        } else {
            logger.debug("[トークン解決] トークンの解決に成功しました。ストラテジーキー: {}", strategyKey);
        }
        return token;
    }

    /**
     * 指定されたキーに対応するストラテジーの存在を判定する
     * 
     * @param key 判定対象キー
     * @return ストラテジーが登録済みの場合 true
     */
    public boolean contains(String key) {
        return strategyMap.containsKey(key);
    }

}
