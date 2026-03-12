package com.collaboportal.common.oauth2.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.oauth2.strategy.login.LoginStrategy;

/**
 * ログインストラテジーを管理するレジストリクラス
 * 基底クラスとして使用され、子クラスで具体的な実装を提供
 */
public class LoginStrategyRegistry {

    Logger logger = LoggerFactory.getLogger(LoginStrategyRegistry.class);

    public LoginStrategyRegistry() {
        logger.debug("LoginStrategyRegistryの初期化が完了しました");
    }

    // ログインストラテジーを保持するマップ
    // キー: ストラテジー識別キー, 値: ログインストラテジー
    private final Map<String, LoginStrategy> strategyMap = new ConcurrentHashMap<>();

    /**
     * 初期化メソッド - 子クラスで必要に応じてオーバーライド可能
     */
    public void init() {
        // デフォルトでは何もしない
        // 子クラスで必要に応じて初期化ロジックを実装
    }

    /**
     * ログインストラテジーを登録する
     * 
     * @param key      ストラテジーを識別するキー
     * @param strategy 登録するログインストラテジー
     */
    public void register(String key, LoginStrategy strategy) {
        logger.debug("[ログインストラテジー登録] 新しいストラテジーを登録します。キー: {}", key);
        strategyMap.put(key, strategy);
        logger.info("[ログインストラテジー登録] ストラテジーの登録が正常に完了しました。キー: {}", key);
    }

    /**
     * 指定されたキーに対応するログインストラテジーを取得する
     * 
     * @param key 取得するストラテジーのキー
     * @return 対応するログインストラテジー
     */
    public LoginStrategy getStrategy(String key) {
        logger.debug("[ログインストラテジー取得] ストラテジーの取得を開始します。キー: {}", key);
        LoginStrategy strategy = strategyMap.get(key);
        if (strategy == null) {
            logger.warn("[ログインストラテジー取得] 指定されたキーに対応するストラテジーが見つかりませんでした。キー: {}", key);
        } else {
            logger.debug("[ログインストラテジー取得] ストラテジーの取得に成功しました。キー: {}", key);
        }
        return strategy;
    }

}
