// package com.collaboportal.common.position;

// import com.collaboportal.common.ConfigManager;
// import com.collaboportal.common.config.PositionCodeConfig;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.Test;
// import org.mockito.MockedStatic;

// import java.util.HashMap;
// import java.util.Map;

// import static org.junit.jupiter.api.Assertions.assertEquals;
// import static org.junit.jupiter.api.Assertions.assertThrows;
// import static org.mockito.Mockito.*;

// /**
//  * PositionCodeResolverの静的初期化が失敗するケース（'other'ルール欠損）のみを検証するテストクラス。
//  * 他のテストへの影響を防ぐため、ファイルが分割されています。
//  */
// class PositionCodeResolverNoOtherInitTest {

//     /**
//      * 異常系: 'other'ルールの欠損
//      * <p>
//      * ルール設定にフォールバック先となるべき 'other' の定義が存在しない状態で初期化された後、
//      * 未定義の役職コードで {@link PositionCodeResolver#get(String)} を呼び出すと
//      * {@link IllegalStateException} がスローされることを検証する。
//      * </p>
//      */
//     @Test
//     @DisplayName("'other'ルールが存在しない場合、フォールバック時に例外がスローされる")
//     void testGet_whenOtherRuleIsMissing() {
//         // 準備: 'other'キーを含まないルールマップを設定
//         PositionCodeConfig mockConfig = mock(PositionCodeConfig.class);
//         Map<String, String> rules = new HashMap<>();
//         rules.put("220", "1,1,0,0,0|1,1,0,0,0"); // 'other'以外のルールは最低一つ必要
//         when(mockConfig.getRules()).thenReturn(rules);

//         try (MockedStatic<ConfigManager> mockedConfigManager = mockStatic(ConfigManager.class)) {
//             mockedConfigManager.when(ConfigManager::getPositionCodeConfig).thenReturn(mockConfig);

//             // 実行と検証: 存在しないコードをget()しようとすると'other'へのフォールバックが発生
//             IllegalStateException e = assertThrows(IllegalStateException.class,
//                     () -> PositionCodeResolver.get("999") // "999"は存在しないため"other"を探しに行く
//             );
//             assertEquals("999のルール存在しません。", e.getMessage());
//         }
//     }
// }
