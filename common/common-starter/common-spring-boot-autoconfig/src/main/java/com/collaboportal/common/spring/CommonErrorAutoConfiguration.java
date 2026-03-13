package com.collaboportal.common.spring;

import org.springframework.boot.autoconfigure.AutoConfiguration;

/**
 * エラー関連の自動設定クラス
 * Spring Bootの自動設定機能を提供する
 */
@AutoConfiguration
public class CommonErrorAutoConfiguration {
    // CustomErrorController は @RestController 注解により自動的に Bean として登録される
    // 追加の設定が必要な場合はここに記述する
}
