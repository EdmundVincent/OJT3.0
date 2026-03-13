package com.collaboportal.common.position;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * PositionRuleクラスの単体テスト。
 * <p>
 * 役割に応じた権限（can）と表示可否（show）の判定ロジックを検証する。
 * </p>
 */
class PositionRuleTest {

    // 統括課長(jobCode: 290)のルール "1,1,1,0,0|1,1,1,1,0" をテストケースとして使用する。
    // このルールは can と show の権限が異なるため、テストに適している。
    private static final int PERM_MASK_TOUKATSU = 7;   // 1+2+4 = 7  (バイナリ: 00111)
    private static final int DEFAULT_MASK_TOUKATSU = 15; // 1+2+4+8+0 = 15 (バイナリ: 01111)

    /**
     * 統括課長（jobCode: 290）の権限ルール
     * <p>
     * canとshowの権限が異なるルールを検証する。
     * </p>
     */
    @Nested
    @DisplayName("統括課長の権限ルール(perm: '1,1,1,0,0', show: '1,1,1,1,0')の場合")
    class ToukatsuKachoRuleTest {

        private final PositionRule rule = new PositionRule(PERM_MASK_TOUKATSU, DEFAULT_MASK_TOUKATSU);

        @DisplayName("can() は permMask '1,1,1,0,0' に基づいて権限を正しく判定すること")
        @ParameterizedTest(name = "Feature.{0} の can() は {1} であるべき")
        @CsvSource({
                "Company,    true",
                "Department, true",
                "Branch,     true",
                "Section,    false",
                "Occupation, false"
        })
        void can_shouldReturnCorrectPermission(Feature feature, boolean expected) {
            assertEquals(expected, rule.can(feature));
        }

        @DisplayName("show() は defaultMask '1,1,1,1,0' に基づいて表示可否を正しく判定すること")
        @ParameterizedTest(name = "Feature.{0} の show() は {1} であるべき")
        @CsvSource({
                "Company,    true",
                "Department, true",
                "Branch,     true",
                "Section,    true",
                "Occupation, false"
        })
        void show_shouldReturnCorrectVisibility(Feature feature, boolean expected) {
            assertEquals(expected, rule.show(feature));
        }

        @Test
        @DisplayName("getPermMask()とgetDefaultMask()は初期値を正しく返すこと")
        void getters_shouldReturnInitialMasks() {
            assertEquals(PERM_MASK_TOUKATSU, rule.getPermMask(), "permMaskが正しく返されること");
            assertEquals(DEFAULT_MASK_TOUKATSU, rule.getDefaultMask(), "defaultMaskが正しく返されること");
        }
    }

    /**
     * 正常系テスト: 全ての権限が与えられていない場合（permMask=0, defaultMask=0）
     * <p>
     * canとshowが常にfalseを返すことを検証する。
     * </p>
     */
    @Nested
    @DisplayName("全ての権限がないルールの場合")
    class NoPermissionRuleTest {

        private final PositionRule rule = new PositionRule(0, 0); // permMask=0, defaultMask=0

        @DisplayName("can() は全てのFeatureに対してfalseを返すこと")
        @ParameterizedTest(name = "Feature.{0} の can() は false であるべき")
        @CsvSource({"Company", "Department", "Branch", "Section", "Occupation"})
        void can_shouldAlwaysReturnFalse(Feature feature) {
            assertEquals(false, rule.can(feature));
        }

        @DisplayName("show() は全てのFeatureに対してfalseを返すこと")
        @ParameterizedTest(name = "Feature.{0} の show() は false であるべき")
        @CsvSource({"Company", "Department", "Branch", "Section", "Occupation"})
        void show_shouldAlwaysReturnFalse(Feature feature) {
            assertEquals(false, rule.show(feature));
        }
    }
}