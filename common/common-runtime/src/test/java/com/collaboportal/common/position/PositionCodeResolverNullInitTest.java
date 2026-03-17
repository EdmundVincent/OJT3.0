// package com.collaboportal.common.position;

// import com.collaboportal.common.ConfigManager;
// import com.collaboportal.common.config.PositionCodeConfig;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.MockedStatic;

// import static org.junit.jupiter.api.Assertions.*;
// import static org.mockito.Mockito.*;

// /**
//  * PositionCodeResolverの静的初期化が失敗するケース（ルールがnullまたは空）のみを検証するテストクラス。
//  * 他のテストへの影響を防ぐため、ファイルが分割されています。
//  */
// class PositionCodeResolverNullInitTest {

//     /**
//      * 異常系: ルール設定がnull
//      * <p>
//      * ルール設定マップそのものがnullの場合に、クラスの静的初期化ブロックで
//      * {@link IllegalStateException} が発生し、それが{@link ExceptionInInitializerError}
//      * でラップされてスローされることを検証する。
//      * </p>
//      */
//     @Test
//     @DisplayName("ルール設定がnullの場合、クラス初期化時に例外がスローされる")
//     void testInitialization_whenRulesAreNull() {
//         // 準備: ConfigManagerがnullを返すように設定
//         PositionCodeConfig mockConfig = mock(PositionCodeConfig.class);
//         when(mockConfig.getRules()).thenReturn(null);

//         try (MockedStatic<ConfigManager> mockedConfigManager = mockStatic(ConfigManager.class)) {
//             mockedConfigManager.when(ConfigManager::getPositionCodeConfig).thenReturn(mockConfig);

//             // 実行と検証: staticブロックでの例外はExceptionInInitializerErrorでラップされる
//             ExceptionInInitializerError e = assertThrows(ExceptionInInitializerError.class,
//                     PositionCodeResolver::snapshot // PositionCodeResolverのクラス初期化をトリガー
//             );

//             // 原因となった例外がIllegalStateExceptionであることを確認
//             Throwable cause = e.getCause();
//             assertInstanceOf(IllegalStateException.class, cause);
//             assertEquals("役職ルールが見つかりません", cause.getMessage());
//         }
//     }


// }
