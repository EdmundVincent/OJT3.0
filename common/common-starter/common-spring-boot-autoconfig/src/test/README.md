# CommonAuthConfiguration 測試說明

本目錄包含 `CommonAuthConfiguration` 類的完整測試套件，提供了多種測試方法和工具類。

## 測試類結構

### 1. CommonAuthConfigurationTest.java
**主要測試類**
- 測試 Bean 創建和配置
- 測試認證邏輯的各種場景
- 測試異常處理
- 測試路徑匹配
- 測試 Spring 上下文集成

### 2. CommonAuthConfigurationIntegrationTest.java
**集成測試類**
- 測試在完整 Spring 上下文中的行為
- 測試依賴注入
- 測試 Bean 生命週期
- 測試並發處理
- 測試不同 HTTP 方法

### 3. CommonAuthConfigurationSimplifiedTest.java
**簡化測試類**
- 使用 TestHelper 輔助類
- 更簡潔的測試代碼
- 展示最佳實踐

### 4. TestHelper.java
**測試輔助類**
- 提供常用的測試工具方法
- 簡化測試環境創建
- 提供斷言工具方法

## 測試覆蓋範圍

### 功能測試
- ✅ Bean 創建和配置
- ✅ 認證邏輯驗證
- ✅ 路徑匹配測試
- ✅ 異常處理測試
- ✅ HTTP 方法支持測試

### 集成測試
- ✅ Spring 上下文集成
- ✅ 依賴注入測試
- ✅ Bean 作用域測試
- ✅ 並發處理測試

### 邊界測試
- ✅ 空值處理
- ✅ 特殊字符處理
- ✅ 異常情況處理

## 運行測試

### 使用 Maven
```bash
# 運行所有測試
mvn test

# 運行特定測試類
mvn test -Dtest=CommonAuthConfigurationTest

# 運行特定測試方法
mvn test -Dtest=CommonAuthConfigurationTest#testAuthServletFilterBeanCreation
```

### 使用 IDE
1. 在 IDE 中打開測試類
2. 右鍵點擊測試方法或類
3. 選擇 "Run Test" 或 "Debug Test"

## 測試配置

### 測試環境配置
測試使用 `application-test.yml` 配置文件，包含：
- 測試環境特定的日誌配置
- 認證相關的測試配置
- 環境標識設置

### 測試依賴
測試依賴以下庫：
- JUnit 5
- Mockito
- Spring Test
- Spring Boot Test

## 測試最佳實踐

### 1. 使用 TestHelper
```java
// 創建測試環境
TestHelper.TestEnvironment env = TestHelper.createNormalityCheckTestEnvironment("self-auth");

// 執行測試
authServletFilter.doFilter(env.getRequest(), env.getResponse(), env.getFilterChain());
```

### 2. 測試異常情況
```java
CommonException exception = assertThrows(CommonException.class, () -> {
    // 執行可能拋出異常的代碼
});
assertEquals(InternalErrorCode.AUTHORIZATION_ERROR, exception.getErrorCode());
```

### 3. 測試正常流程
```java
assertDoesNotThrow(() -> {
    // 執行應該正常完成的代碼
});
```

### 4. 驗證請求/響應
```java
// 驗證請求頭
TestHelper.assertRequestHeader(request, "Authorization-Type", "self-auth");

// 驗證響應狀態
TestHelper.assertResponseStatus(response, 200);
```

## 測試數據

### 測試路徑
- `/api/v1/normality-check` - 包含的認證路徑
- `/api/v1/other-endpoint` - 非包含路徑

### 測試認證類型
- `null` - 無認證（應拋出異常）
- `"self-auth"` - 自定義認證
- `"other-auth"` - 其他認證類型
- `""` - 空字符串認證

### HTTP 方法
- GET, POST, PUT, DELETE, PATCH

## 故障排除

### 常見問題

1. **測試失敗：找不到類**
   - 確保所有依賴都已正確配置
   - 檢查 Maven 依賴是否包含測試依賴

2. **Spring 上下文初始化失敗**
   - 檢查測試配置類是否正確
   - 確保測試配置文件路徑正確

3. **Mock 對象問題**
   - 確保正確導入了 Mockito 依賴
   - 檢查 Mock 對象的設置

### 調試技巧

1. **啟用詳細日誌**
   ```yaml
   logging:
     level:
       com.collaboportal.common.spring.common: DEBUG
   ```

2. **使用斷點調試**
   - 在測試方法中設置斷點
   - 使用 IDE 的調試功能

3. **檢查測試報告**
   - 查看測試執行報告
   - 分析失敗的測試用例

## 擴展測試

### 添加新的測試用例
1. 在適當的測試類中添加新的測試方法
2. 使用 `@Test` 註解標記測試方法
3. 遵循現有的測試模式

### 添加新的測試輔助方法
1. 在 `TestHelper` 類中添加新的工具方法
2. 確保方法具有適當的文檔註釋
3. 為新方法添加單元測試

### 添加新的測試配置
1. 在 `application-test.yml` 中添加新的配置項
2. 在測試中使用 `@TestPropertySource` 或 `@ActiveProfiles`

## 維護說明

### 更新測試
當 `CommonAuthConfiguration` 類發生變化時：
1. 更新相關的測試用例
2. 添加新的測試場景
3. 確保測試覆蓋率不降低

### 測試覆蓋率
定期檢查測試覆蓋率：
```bash
mvn test jacoco:report
```

### 持續集成
確保測試在 CI/CD 流程中自動運行：
- 提交代碼時自動運行測試
- 測試失敗時阻止合併
- 定期生成測試報告 