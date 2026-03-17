package com.collaboportal.common.position;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.config.PositionCodeConfig;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PositionCodeResolverTest {

    @BeforeAll
    void beforeAll() {
        // PositionCodeResolverの静的初期化ブロックで読み込まれる設定をモックで定義する。
        // can(変更可否): 1=不可, 0=可 (フロントエンドの表示制御に合わせる)
        // show(表示): 1=可, 0=不可
        Map<String, String> rules = new HashMap<>();

        // --- application-common.properties から読み込んだ本番ルール ---
        rules.put("220", "1,1,0,0,0|1,1,0,0,0");         // 営業部長
        rules.put("222", "1,1,0,0,0|1,1,0,0,0");         // 主管部長
        rules.put("225", "1,1,0,0,0|1,1,0,0,0");         // 副営業部長
        rules.put("250", "1,1,1,0,0|1,1,1,0,0");         // 支店長
        rules.put("255", "1,1,1,0,0|1,1,1,0,0");         // 副支店長
        rules.put("280", "1,1,1,0,0|1,1,1,0,0");         // 営業所長
        rules.put("290", "1,1,1,0,0|1,1,1,1,0");         // 統括課長
        rules.put("850", "1,1,1,0,0|1,1,1,1,1");         // 社員
        rules.put("855", "1,1,1,0,0|1,1,1,1,1");         // 受入出向
        rules.put("870", "1,1,1,0,0|1,1,1,1,1");         // 契約社員
        rules.put("headquarters", "0,0,0,0,0|0,0,0,0,0"); // 本部
        rules.put("other", "1,1,1,1,1|1,1,1,1,1");         // 「その他」(フォールバック先)

        // --- テスト用の特殊なルール ---
        rules.put("250_special", "1,1,1,1,0|1,1,1,1,0"); // 支店長（特別コンテキスト用）
        rules.put("bad_format", "1,1,1|1,1,1");             // 不正なフォーマット (要素数不足)
        rules.put("bad_format_no_pipe", "1,1,1,1,1");       // 不正なフォーマット (パイプなし)

        PositionCodeConfig positionCodeConfig = new PositionCodeConfig().setRules(rules);
        ConfigManager.setConfig(positionCodeConfig);
    }

    @BeforeEach
    void setUp() throws Exception {
        // 各テストの前にキャッシュをクリアし、テストの独立性を高める。
        Field cacheField = PositionCodeResolver.class.getDeclaredField("CACHE");
        cacheField.setAccessible(true);
        ((ConcurrentHashMap<?, ?>) cacheField.get(null)).clear();
    }

    /**
     * 正常系: 本番定義された全ルールの網羅的検証
     * <p>
     * application-common.propertiesに定義されている全てのルールが、
     * 意図した通りの権限設定に解決されることを一括で検証する。
     * </p>
     */
    @DisplayName("本番定義された全ルールの権限を網羅的に検証する")
    @ParameterizedTest(name = "[{index}] {3} ({0})")
    @CsvSource({
            // jobCode,        can(C,D,B,S,O), show(C,D,B,S,O), description
            "'220',        '1,1,0,0,0',    '1,1,0,0,0',    '営業部長'",
            "'222',        '1,1,0,0,0',    '1,1,0,0,0',    '主管部長'",
            "'225',        '1,1,0,0,0',    '1,1,0,0,0',    '副営業部長'",
            "'250',        '1,1,1,0,0',    '1,1,1,0,0',    '支店長'",
            "'255',        '1,1,1,0,0',    '1,1,1,0,0',    '副支店長'",
            "'280',        '1,1,1,0,0',    '1,1,1,0,0',    '営業所長'",
            "'290',        '1,1,1,0,0',    '1,1,1,1,0',    '統括課長'",
            "'850',        '1,1,1,0,0',    '1,1,1,1,1',    '社員'",
            "'855',        '1,1,1,0,0',    '1,1,1,1,1',    '受入出向'",
            "'870',        '1,1,1,0,0',    '1,1,1,1,1',    '契約社員'",
            "'headquarters', '0,0,0,0,0',    '0,0,0,0,0',    '本部'",
            "'other',      '1,1,1,1,1',    '1,1,1,1,1',    'その他'"
    })
    void verifyAllProductionRules(String jobCode, String canPermissions, String showPermissions, String description) {
        PositionRule rule = PositionCodeResolver.get(jobCode);

        String[] canParts = canPermissions.split(",");
        boolean[] canExpected = new boolean[canParts.length];
        for (int i = 0; i < canParts.length; i++) {
            canExpected[i] = "1".equals(canParts[i].trim());
        }

        String[] showParts = showPermissions.split(",");
        boolean[] showExpected = new boolean[showParts.length];
        for (int i = 0; i < showParts.length; i++) {
            showExpected[i] = "1".equals(showParts[i].trim());
        }

        // can(変更可否)の権限を検証
        assertEquals(canExpected[0], rule.can(Feature.Company), description + " - can(Company)");
        assertEquals(canExpected[1], rule.can(Feature.Department), description + " - can(Department)");
        assertEquals(canExpected[2], rule.can(Feature.Branch), description + " - can(Branch)");
        assertEquals(canExpected[3], rule.can(Feature.Section), description + " - can(Section)");
        assertEquals(canExpected[4], rule.can(Feature.Occupation), description + " - can(Occupation)");

        // show(表示)の権限を検証
        assertEquals(showExpected[0], rule.show(Feature.Company), description + " - show(Company)");
        assertEquals(showExpected[1], rule.show(Feature.Department), description + " - show(Department)");
        assertEquals(showExpected[2], rule.show(Feature.Branch), description + " - show(Branch)");
        assertEquals(showExpected[3], rule.show(Feature.Section), description + " - show(Section)");
        assertEquals(showExpected[4], rule.show(Feature.Occupation), description + " - show(Occupation)");
    }


    /**
     * 正常系: コンテキスト付きルールの優先解決
     * <p>
     * "250" と "250_special" のように通常ルールとコンテキスト付きルールが両方存在する場合、
     * コンテキストを指定してget()を呼び出すと、コンテキスト付きルールが優先的に使用されることを検証する。
     * </p>
     */
    @Test
    @DisplayName("支店長(jobCode: 250)にcontext付きでアクセスした場合、優先して解決されること")
    void get_shouldPrioritizeContextMatch() {
        // テスト項目: "250"と"250_special"の両ルールが存在する場合、context付きのgetが"250_special"を優先するか。
        // 期待結果: 通常の支店長(250)では権限がないSectionが、specialコンテキストでは権限を持つこと。
        PositionRule rule = PositionCodeResolver.get("250", "special");
        assertTrue(rule.can(Feature.Branch)); // "250"ルールでもtrue
        assertTrue(rule.can(Feature.Section), "specialルールが適用され、Section権限を持つはず");
    }

    /**
     * 正常系: フォールバック機能の検証
     * <p>
     * 定義されていない役職コード ("999") で解決を試みた場合に、
     * "other" として定義されたデフォルトのルールが適用されることを検証する。
     * </p>
     */
    @Test
    @DisplayName("未定義の役職コード(jobCode: 999)の場合、'other'ルールにフォールバックすること")
    void get_shouldFallbackToOtherRule() {
        // テスト項目: モックに存在しないjobCode "999" を指定した場合の動作。
        // 期待結果: フォールバック機能により "other" ルール("1,1,1,1,1|1,1,1,1,1")が適用されること。
        PositionRule rule = PositionCodeResolver.get("999");
        assertTrue(rule.can(Feature.Company));
        assertTrue(rule.can(Feature.Department), "'other'ルールが変更されたため、trueになるはず");
        assertTrue(rule.can(Feature.Branch));
        assertTrue(rule.can(Feature.Section));
        assertTrue(rule.can(Feature.Occupation));

        assertTrue(rule.show(Feature.Company), "'other'のshowマスクはすべて1のはず");
        assertTrue(rule.show(Feature.Department));
        assertTrue(rule.show(Feature.Branch));
        assertTrue(rule.show(Feature.Section));
        assertTrue(rule.show(Feature.Occupation));
    }

    /**
     * 正常系: キャッシュ機能の検証
     * <p>
     * 同じ役職コードで複数回解決を試みた場合に、2回目以降はキャッシュされた
     * 同一のインスタンスが返却されることを検証する。
     * </p>
     */
    @Test
    @DisplayName("ルール解決がキャッシュされ、同一のオブジェクトが返されること")
    void get_shouldCacheRuleResolution() {
        // テスト項目: 同じjobCodeでgetを2回呼び出す。
        // 期待結果: 2回目に返されるPositionRuleオブジェクトが、1回目と同一のインスタンスであること。
        PositionRule rule1 = PositionCodeResolver.get("250");
        PositionRule rule2 = PositionCodeResolver.get("250");
        assertSame(rule1, rule2, "キャッシュが効いていれば、同一のオブジェクトが返されるはず");
    }


    /**
     * 正常系: snapshot()メソッドの検証
     * <p>
     * snapshot()メソッドが、現在ロードされているルールの不変なスナップショットを
     * 正しく返却することを検証する。
     * </p>
     */
    @Test
    @DisplayName("snapshot()がルールのスナップショットを返すこと")
    void snapshot_shouldReturnImmutableMapOfRules() {
        Map<String, String> snapshot = PositionCodeResolver.snapshot();
        assertNotNull(snapshot);
        // 12(本番) + 3(テスト用特殊) = 15
        assertEquals(15, snapshot.size());
        assertEquals("1,1,0,0,0|1,1,0,0,0", snapshot.get("220"));
        // スナップショットが変更不可能であることを検証
        assertThrows(UnsupportedOperationException.class, () -> snapshot.put("new", "rule"));
    }

    /**
     * 正常系: 空のコンテキストでのget()
     * <p>
     * contextに空文字列を渡した場合、コンテキストなしのget()と同じように
     * 基本的なjobCodeでルールが解決されることを検証する。
     * </p>
     */
    @Test
    @DisplayName("空文字列のcontextでget()を呼び出すと、通常のjobCodeで解決されること")
    void get_shouldResolveWithBaseJobCode_whenContextIsEmpty() {
        PositionRule ruleWithEmptyContext = PositionCodeResolver.get("220", "");
        PositionRule ruleWithoutContext = PositionCodeResolver.get("220");
        assertSame(ruleWithoutContext, ruleWithEmptyContext, "空コンテキストはコンテキストなしと同じルールを返すべき");
    }

    /**
     * 正常系: 存在しないコンテキストでのフォールバック
     * <p>
     * 存在しないcontextを指定してget()を呼び出した場合、基本的なjobCodeのルールに
     * フォールバックして解決されることを検証する。
     * </p>
     */
    @Test
    @DisplayName("存在しないcontextでget()を呼び出すと、通常のjobCodeにフォールバックすること")
    void get_shouldFallbackToBaseJobCode_whenContextIsNotFound() {
        PositionRule ruleWithNonExistentContext = PositionCodeResolver.get("220", "non_existent_context");
        PositionRule ruleWithoutContext = PositionCodeResolver.get("220");
        assertSame(ruleWithoutContext, ruleWithNonExistentContext, "存在しないコンテキストは通常のjobCodeにフォールバックすべき");
    }

    /**
     * 異常系: 不正なフォーマットのルール
     * <p>
     * "1,1,1|1,1,1" のように、ルールが不正なフォーマットで定義されている場合に、
     * 解決を試みると {@link IllegalArgumentException} がスローされることを検証する。
     * </p>
     */
    @Test
    @DisplayName("不正なフォーマットのルールを解決しようとするとIllegalArgumentExceptionをスローすること")
    void parse_shouldThrowExceptionForBadRuleFormat() {
        // テスト項目: 不正なフォーマット(要素数が5ではない)を持つ "bad_format" ルールを解決しようとする。
        // 期待結果: parse処理の途中でIllegalArgumentExceptionがスローされること。
        assertThrows(IllegalArgumentException.class, () -> {
            PositionCodeResolver.get("bad_format");
        });
    }

    /**
     * 異常系: 不正なフォーマットのルール (パイプなし)
     * <p>
     * ルール内に "|" が含まれない不正なフォーマットの場合に、
     * 解決を試みると {@link IllegalArgumentException} がスローされることを検証する。
     * </p>
     */
    @Test
    @DisplayName("パイプがない不正なフォーマットのルールでIllegalArgumentExceptionをスローすること")
    void parse_shouldThrowExceptionForBadRuleFormat_whenNoPipe() {
        assertThrows(IllegalArgumentException.class, () -> {
            PositionCodeResolver.get("bad_format_no_pipe");
        });
    }
}
