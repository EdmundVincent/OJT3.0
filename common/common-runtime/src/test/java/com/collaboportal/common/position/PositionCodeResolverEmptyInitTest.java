// package com.collaboportal.common.position;

// import com.collaboportal.common.ConfigManager;
// import com.collaboportal.common.config.PositionCodeConfig;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.MockedStatic;

// import java.util.Collections;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// /**
//  * PositionCodeResolverの静的初期化が失敗するケース（ルールが空）のみを検証するテストクラス。
//  * 他のテストへの影響を防ぐため、ファイルが分割されています。
//  */
// class PositionCodeResolverEmptyInitTest {

//     /**
//      * 異常系: ルール設定が空
//      * <p>
//      * ルール設定マップが空の場合に、クラスの静的初期化ブロックで
//      * {@link IllegalStateException} が発生し、それが{@link ExceptionInInitializerError}
//      * でラップされてスローされることを検証する。
//      * </p>
//      */
//     @Test
//     @DisplayName("ルール設定が空マップの場合、クラス初期化時に例外がスローされる")
//     void testInitialization_whenRulesAreEmpty() {
//         // 準備: ConfigManagerが空のMapを返すように設定
//         PositionCodeConfig mockConfig = mock(PositionCodeConfig.class);
//         when(mockConfig.getRules()).thenReturn(Collections.emptyMap());

//         try (MockedStatic<ConfigManager> mockedConfigManager = mockStatic(ConfigManager.class)) {
//             mockedConfigManager.when(ConfigManager::getPositionCodeConfig).thenReturn(mockConfig);

//             // 実行と検証
//             ExceptionInInitializerError e = assertThrows(ExceptionInInitializerError.class,
//                     PositionCodeResolver::snapshot // PositionCodeResolverのクラス初期化をトリガー
//             );

//             Throwable cause = e.getCause();
//             assertInstanceOf(IllegalStateException.class, cause);
//             assertEquals("役職ルールが見つかりません", cause.getMessage());
//         }
//     }
// }
