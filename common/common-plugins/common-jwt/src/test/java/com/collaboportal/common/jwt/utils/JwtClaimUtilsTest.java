package com.collaboportal.common.jwt.utils;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.config.CommonConfig;
import com.collaboportal.common.config.PositionCodeConfig;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.position.PositionCodeResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JwtClaimUtilsTest {

    private static final String DUMMY_TOKEN = "dummy-auth-token";

    private static MockedStatic<ConfigManager> configManagerMock;
    private static final String propertiesContent = "#役割コードに紐づく変更可否情報|デフォルト表示情報\n" +
            "#役割コードが営業部長の場合\n" +
            "util.collaboportal.positioncode.220 = 1,1,0,0,0|1,1,0,0,0\n" +
            "#役割コードが主管部長の場合\n" +
            "util.collaboportal.positioncode.222 = 1,1,0,0,0|1,1,0,0,0\n" +
            "#役割コードが副営業部長の場合\n" +
            "util.collaboportal.positioncode.225 = 1,1,0,0,0|1,1,0,0,0\n" +
            "#役割コードが支店長の場合\n" +
            "util.collaboportal.positioncode.250 = 1,1,1,0,0|1,1,1,0,0\n" +
            "#役割コードが副支店長の場合\n" +
            "util.collaboportal.positioncode.255 = 1,1,1,0,0|1,1,1,0,0\n" +
            "#役割コードが営業所長の場合\n" +
            "util.collaboportal.positioncode.280 = 1,1,1,0,0|1,1,1,0,0\n" +
            "#役割コードが統括課長の場合\n" +
            "util.collaboportal.positioncode.290 = 1,1,1,0,0|1,1,1,1,0\n" +
            "#役割コードが社員の場合\n" +
            "util.collaboportal.positioncode.850 = 1,1,1,0,0|1,1,1,1,1\n" +
            "#役割コードが受入出向の場合\n" +
            "util.collaboportal.positioncode.855 = 1,1,1,0,0|1,1,1,1,1\n" +
            "#役割コードが契約社員の場合\n" +
            "util.collaboportal.positioncode.870 = 1,1,1,0,0|1,1,1,1,1\n" +
            "#本部の場合\n" +
            "util.collaboportal.positioncode.headquarters = 0,0,0,0,0|0,0,0,0,0\n" +
            "#「その他」の場合\n" +
            "util.collaboportal.positioncode.other = 1,1,1,1,1|1,1,1,1,1";

    @BeforeAll
    static void beforeAll() throws IOException {
        configManagerMock = mockStatic(ConfigManager.class);

        // --- PositionCodeResolverの初期化が成功するように、実際のルールを読み込んでモック化 ---
        PositionCodeConfig mockPositionCodeConfig = mock(PositionCodeConfig.class);

        Properties props = new Properties();
        props.load(new StringReader(propertiesContent));

        Map<String, String> rules = new HashMap<>();
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith("util.collaboportal.positioncode.")) {
                String jobCode = key.substring("util.collaboportal.positioncode.".length());
                rules.put(jobCode, props.getProperty(key));
            }
        }

        when(mockPositionCodeConfig.getRules()).thenReturn(rules);
        configManagerMock.when(ConfigManager::getPositionCodeConfig).thenReturn(mockPositionCodeConfig);

        // --- JwtClaimUtils内のsecretKey()でNPEが発生しないようにCommonConfigをモック ---
        CommonConfig mockCommonConfig = mock(CommonConfig.class);
        String dummySecret = "c29tZUR1bW15U2VjcmV0S2V5VGhhdElzTG9uZ0Vub3VnaEZvckhTMjU2QWxnb3JpdGht";
        when(mockCommonConfig.getSecretKey()).thenReturn(dummySecret);
        configManagerMock.when(ConfigManager::getConfig).thenReturn(mockCommonConfig);
    }

    @AfterAll
    static void afterAll() {
        configManagerMock.close();
    }

    @BeforeEach
    void setUp() throws Exception {
        // PositionCodeResolverのキャッシュをクリアし、テストの独立性を高める
        Field cacheField = PositionCodeResolver.class.getDeclaredField("CACHE");
        cacheField.setAccessible(true);
        ((ConcurrentHashMap<?, ?>) cacheField.get(null)).clear();
    }

    /**
     * 正常系テスト: 本部ユーザー (role: 1)
     * <p>
     * ユーザーのロールが本部（role: 1）の場合に、getDepartCodesメソッドが
     * 固定の空文字列（",,,,"）を返すことを検証する。
     * </p>
     */
    @Test
    @DisplayName("本部ユーザー(role: 1)の場合、固定の空文字列を返すこと")
    void getDepartCodes_shouldReturnFixedString_forHonbuUser() {
        // --- 準備 ---
        try (MockedStatic<CommonHolder> commonHolderMock = mockStatic(CommonHolder.class);
                MockedStatic<JwtClaimUtils> jwtUtilsMock = mockStatic(JwtClaimUtils.class,
                        Mockito.CALLS_REAL_METHODS)) {

            BaseRequest mockRequest = mock(BaseRequest.class);
            when(mockRequest.getCookieValue("AuthToken")).thenReturn(DUMMY_TOKEN);
            commonHolderMock.when(CommonHolder::getRequest).thenReturn(mockRequest);

            // getDepartCodesの中で呼ばれるメソッドをすべてスタブする
            jwtUtilsMock.when(() -> JwtClaimUtils.getRoleAsInt(DUMMY_TOKEN)).thenReturn(Optional.of(1));
            // 以下のgetはif分岐に入れば呼ばれないが、呼ばれる可能性のあるものはすべて定義しておく
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "jobCode", String.class))
                    .thenReturn(Optional.empty());
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "userId", String.class))
                    .thenReturn(Optional.empty());
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "companyCode", String.class))
                    .thenReturn(Optional.empty());
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "departmentCode", String.class))
                    .thenReturn(Optional.empty());
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "branchCode", String.class))
                    .thenReturn(Optional.empty());
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "sectionCode", String.class))
                    .thenReturn(Optional.empty());

            // --- 実行 ---
            String result = JwtClaimUtils.getDepartCodes();

            // --- 検証 ---
            assertEquals("", result);
        }
    }

    /**
     * 正常系テスト: 各役割コードに応じたマスキング
     * <p>
     * application-common.propertiesに定義された各役割コード（jobCode）に対し、
     * PositionRuleに基づいて部署関連のコード群が正しくマスキングされた文字列を返すことを検証する。
     * </p>
     */
    @DisplayName("各jobCodeに応じて、ルール通りに部署コードがマスキングされること")
    @ParameterizedTest(name = "jobCode: {0} の場合、期待結果は {1}")
    @CsvSource({
            // 営業部長レベル (Company, Department)
            "220, 'C01,D02,,,'",
            "222, 'C01,D02,,,'",
            "225, 'C01,D02,,,'",
            // 支店長レベル (Company, Department, Branch)
            "250, 'C01,D02,B03,,'",
            "255, 'C01,D02,B03,,'",
            "280, 'C01,D02,B03,,'",
            // 統括課長レベル (canは支店まで)
            "290, 'C01,D02,B03,,'",
            // 社員レベル (canは支店まで)
            "850, 'C01,D02,B03,,'",
            "855, 'C01,D02,B03,,'",
            "870, 'C01,D02,B03,,'",
            // 未定義のコードは 'other' にフォールバックする
            "999, 'C01,D02,B03,S04,U05'"
    })
    void getDepartCodes_shouldReturnMaskedCodes_forEachJobCode(String jobCode, String expected) {
        // --- 準備 ---
        try (MockedStatic<CommonHolder> commonHolderMock = mockStatic(CommonHolder.class);
                MockedStatic<JwtClaimUtils> jwtUtilsMock = mockStatic(JwtClaimUtils.class,
                        Mockito.CALLS_REAL_METHODS)) {

            BaseRequest mockRequest = mock(BaseRequest.class);
            when(mockRequest.getCookieValue("AuthToken")).thenReturn(DUMMY_TOKEN);
            commonHolderMock.when(CommonHolder::getRequest).thenReturn(mockRequest);

            // getDepartCodesの中で呼ばれるpublic staticメソッドをスタブ
            jwtUtilsMock.when(() -> JwtClaimUtils.getRoleAsInt(DUMMY_TOKEN)).thenReturn(Optional.of(8)); // 本部以外(非本部)
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "jobCode", String.class))
                    .thenReturn(Optional.of(jobCode));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "companyCode", String.class))
                    .thenReturn(Optional.of("C01"));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "departmentCode", String.class))
                    .thenReturn(Optional.of("D02"));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "branchCode", String.class))
                    .thenReturn(Optional.of("B03"));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "sectionCode", String.class))
                    .thenReturn(Optional.of("S04"));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "userId", String.class))
                    .thenReturn(Optional.of("U05"));

            // --- 実行 ---
            String result = JwtClaimUtils.getDepartCodes();

            // --- 検証 ---
            assertEquals(expected, result);
        }
    }

    /**
     * 正常系テスト: JWTクレームの一部がnull
     * <p>
     * JWTから取得した部署関連コードの一部がnullの場合でも、
     * 空文字列として正しく処理され、意図した形式の文字列が返されることを検証する。
     * </p>
     */
    @Test
    @DisplayName("JWTクレームの一部がnullの場合でも、正しく空文字列として処理されること")
    void getDepartCodes_shouldHandleNullClaimValuesGracefully() {
        // --- 準備 ---
        try (MockedStatic<CommonHolder> commonHolderMock = mockStatic(CommonHolder.class);
                MockedStatic<JwtClaimUtils> jwtUtilsMock = mockStatic(JwtClaimUtils.class,
                        Mockito.CALLS_REAL_METHODS)) {

            final String jobCode = "250"; // 支店長レベル (Company, Department, Branchまで表示)
            final String expected = "C01,,B03,,";
            final int NON_HONBU_ROLE = 8; // 下位ビットが0 => 非本部

            BaseRequest mockRequest = mock(BaseRequest.class);
            when(mockRequest.getCookieValue("AuthToken")).thenReturn(DUMMY_TOKEN);
            commonHolderMock.when(CommonHolder::getRequest).thenReturn(mockRequest);

            // getDepartCodesの中で呼ばれるpublic staticメソッドをスタブ
            jwtUtilsMock.when(() -> JwtClaimUtils.getRoleAsInt(DUMMY_TOKEN)).thenReturn(Optional.of(NON_HONBU_ROLE));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "jobCode", String.class))
                    .thenReturn(Optional.of(jobCode));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "companyCode", String.class))
                    .thenReturn(Optional.of("C01"));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "departmentCode", String.class))
                    .thenReturn(Optional.empty()); // departmentCodeがnull
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "branchCode", String.class))
                    .thenReturn(Optional.of("B03"));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "sectionCode", String.class))
                    .thenReturn(Optional.empty()); // sectionCodeがnull
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "userId", String.class))
                    .thenReturn(Optional.of("U05")); // 表示権限がないので結果には影響しない

            // --- 実行 ---
            String result = JwtClaimUtils.getDepartCodes();

            // --- 検証 ---
            assertEquals(expected, result);
        }
    }

    /**
     * 正常系テスト: roleクレームがnull
     * <p>
     * JWTにroleクレームが含まれず、roleがnullになるケース。
     * 本部ユーザー(role=1)の特別扱いをされず、jobCodeに基づいた
     * 通常の権限解決ロジックが実行されることを検証する。
     * </p>
     */
    @Test
    @DisplayName("roleクレームがnullの場合、jobCodeのルールで解決されること")
    void getDepartCodes_shouldUsePositionRule_whenRoleIsNull() {
        // --- 準備 ---
        try (MockedStatic<CommonHolder> commonHolderMock = mockStatic(CommonHolder.class);
                MockedStatic<JwtClaimUtils> jwtUtilsMock = mockStatic(JwtClaimUtils.class,
                        Mockito.CALLS_REAL_METHODS)) {

            final String jobCode = "250"; // 支店長レベル
            final String expected = "C01,D02,B03,,";

            BaseRequest mockRequest = mock(BaseRequest.class);
            when(mockRequest.getCookieValue("AuthToken")).thenReturn(DUMMY_TOKEN);
            commonHolderMock.when(CommonHolder::getRequest).thenReturn(mockRequest);

            // roleがnullになるように設定
            jwtUtilsMock.when(() -> JwtClaimUtils.getRoleAsInt(DUMMY_TOKEN)).thenReturn(Optional.empty());
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "jobCode", String.class))
                    .thenReturn(Optional.of(jobCode));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "companyCode", String.class))
                    .thenReturn(Optional.of("C01"));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "departmentCode", String.class))
                    .thenReturn(Optional.of("D02"));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "branchCode", String.class))
                    .thenReturn(Optional.of("B03"));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "sectionCode", String.class))
                    .thenReturn(Optional.of("S04"));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "userId", String.class))
                    .thenReturn(Optional.of("U05"));

            // --- 実行 ---
            String result = JwtClaimUtils.getDepartCodes();

            // --- 検証 ---
            assertEquals(expected, result);
        }
    }

    /**
     * 正常系テスト: jobCodeクレームがnull
     * <p>
     * JWTにjobCodeクレームが含まれず、jobCodeがnullになるケース。
     * {@link com.collaboportal.common.position.PositionCodeResolver} が 'other'
     * ルールにフォールバックし、
     * 'other' ルールの権限（通常は全表示）が適用されることを検証する。
     * </p>
     */
    @Test
    @DisplayName("jobCodeクレームがnullの場合、'other'ルールで解決されること")
    void getDepartCodes_shouldUseOtherRule_whenJobCodeIsNull() {
        // --- 準備 ---
        try (MockedStatic<CommonHolder> commonHolderMock = mockStatic(CommonHolder.class);
                MockedStatic<JwtClaimUtils> jwtUtilsMock = mockStatic(JwtClaimUtils.class,
                        Mockito.CALLS_REAL_METHODS)) {

            final String expected = "C01,D02,B03,S04,U05"; // 'other'ルールでは全て表示

            BaseRequest mockRequest = mock(BaseRequest.class);
            when(mockRequest.getCookieValue("AuthToken")).thenReturn(DUMMY_TOKEN);
            commonHolderMock.when(CommonHolder::getRequest).thenReturn(mockRequest);

            // jobCodeがnullになるように設定
            jwtUtilsMock.when(() -> JwtClaimUtils.getRoleAsInt(DUMMY_TOKEN)).thenReturn(Optional.of(8)); // 本部以外
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "jobCode", String.class))
                    .thenReturn(Optional.empty());
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "companyCode", String.class))
                    .thenReturn(Optional.of("C01"));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "departmentCode", String.class))
                    .thenReturn(Optional.of("D02"));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "branchCode", String.class))
                    .thenReturn(Optional.of("B03"));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "sectionCode", String.class))
                    .thenReturn(Optional.of("S04"));
            jwtUtilsMock.when(() -> JwtClaimUtils.get(DUMMY_TOKEN, "userId", String.class))
                    .thenReturn(Optional.of("U05"));

            // --- 実行 ---
            String result = JwtClaimUtils.getDepartCodes();

            // --- 検証 ---
            assertEquals(expected, result);
        }
    }

    /**
     * 異常系テスト: 認証トークンが存在しない (null)
     * <p>
     * AuthTokenクッキーが存在しない場合に、getDepartCodesメソッドがnullを返すことを検証する。
     * </p>
     */
    @Test
    @DisplayName("認証トークンが存在しない場合、nullを返すこと")
    void getDepartCodes_shouldReturnNull_whenTokenIsMissing() {
        // --- 準備 ---
        try (MockedStatic<CommonHolder> commonHolderMock = mockStatic(CommonHolder.class)) {
            BaseRequest mockRequest = mock(BaseRequest.class);
            when(mockRequest.getCookieValue("AuthToken")).thenReturn(null);
            commonHolderMock.when(CommonHolder::getRequest).thenReturn(mockRequest);

            // --- 実行 ---
            String result = JwtClaimUtils.getDepartCodes();

            // --- 検証 ---
            assertNull(result);
        }
    }

    /**
     * 異常系テスト: 認証トークンが空文字列
     * <p>
     * AuthTokenクッキーが空文字列の場合に、getDepartCodesメソッドがnullを返すことを検証する。
     * </p>
     */
    @Test
    @DisplayName("認証トークンが空文字列の場合、nullを返すこと")
    void getDepartCodes_shouldReturnNull_whenTokenIsEmpty() {
        // --- 準備 ---
        try (MockedStatic<CommonHolder> commonHolderMock = mockStatic(CommonHolder.class)) {
            BaseRequest mockRequest = mock(BaseRequest.class);
            when(mockRequest.getCookieValue("AuthToken")).thenReturn("");
            commonHolderMock.when(CommonHolder::getRequest).thenReturn(mockRequest);

            // --- 実行 ---
            String result = JwtClaimUtils.getDepartCodes();

            // --- 検証 ---
            assertNull(result);
        }
    }

}
