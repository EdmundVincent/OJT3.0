package com.collaboportal.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 内部エラーコードを定義する列挙型
 * 各エラーコードはエラーメッセージとエラーIDを持つ
 */
@AllArgsConstructor
public enum InternalErrorCode {
    // 未定義エラー
    UNDEINED_ERROR(500, "Undefined", "ERROR_ID_000"),
    // 認証証明書エラー
    ATTESTATION_CERTIFICATE_ERROR(500, "NotAuthenticated", "ERROR_ID_001"),
    // バリデーションエラー
    VALIDATION_ERROR(400, "ValidationException", "ERROR_ID_002"),
    // レコード未検出エラー
    RECORD_NOT_FOUND_ERROR(404, "RecordNotFoundException", "ERROR_ID_003"),
    // 楽観的ロック失敗エラー
    OPTIMISTIC_LOCKING_FAILURE_ERROR(500, "OptimisticLockingFailureException", "ERROR_ID_004"),
    // システムエラー
    SYSTEM_ERROR(500, "SystemException", "ERROR_ID_100"),
    // 認可エラー
    AUTHORIZATION_ERROR(401, "AuthorizationException", "ERROR_ID_101"),

    // 患者情報 START
    // 患者情報登録API専用エラー
    // 重複エラー：エラーコード409を返す。入力した患者ID、企画IDが既存の患者IDと重複していた場合。
    PATIENT_ID_DUPLICATE_ERROR(409, "PatientIdDuplicateException", "ERROR_ID_200"),
    // 患者情報 END

    // 納品可否管理 START
    // 納品可否依頼入力・納品可否依頼内容修正API専用エラー
    // リクエストエラー1：エラーコード4041を返す。入力した患者IDが該当のメーカー・製品に存在しない場合。
    PATIENT_ID_NOT_FOUND_FOR_PATIENT_ERROR(4041, "PatientIdNotFoundForPatientException", "ERROR_ID_300"),
    // リクエストエラー2：エラーコード4042を返す。入力した納品先得意先コードがスズケン得意先マスタに存在しない場合
    TOK_COD_NOT_FOUND_ERROR(4042, "TokCodNotFoundException", "ERROR_ID_301"),
    // リクエストエラー3：エラーコード4043を返す。選択した企画の有効期限が切れている場合
    KIKAKU_EXPIRED_ERROR(4043, "KikakuExpiredException", "ERROR_ID_302"),
    // 納品可否管理 END

    // 状況一覧 START
    // 症例情報新規登録・症例情報更新API専用エラー
    // リクエストエラー(投与(処方)回数)：エラーコード4002を返す。投与(処方)回数が患者ID単位で過去の[症例情報].[投与回数]のうち最大数＋１を超える回数。
    TOYO_COUNT_INVALID_ERROR(4002, "ToyoCountInvalidException", "ERROR_ID_400"),
    // リクエストエラー(次回投与(処方)予定日)：エラーコード4003を返す。次回投与(処方)予定日が処方日より以前の日付。
    NEXT_TOYO_YMD_INVALID_ERROR(4003, "NextToyoYmdInvalidException", "ERROR_ID_401"),

    // 患者ID登録API専用エラー
    // リクエストエラー(患者ID)：エラーコード4001を返す。入力された「患者ID」が納品可否判断情報の「納品可」の中に存在しない。
    PATIENT_ID_NOT_FOUND_FOR_DELIVERY_ERROR(4001, "PatientIdNotFoundForDeliveryException", "ERROR_ID_410"),
    // リクエストエラー(患者ID・得意先コード)：エラーコード4002を返す。入力された「患者ID」と販売実績データの「得意先コード」が納品可否判断情報の「納品可」の中に存在しない。
    PATIENT_ID_TOK_COD_NOT_FOUND_ERROR(4002, "PatientIdTokCodNotFoundException", "ERROR_ID_411"),
    // リクエストエラー(受注取消不可)：エラーコード4001を返す。受注取消不可のデータ。

    // 受注取消・返品API専用エラー
    // リクエストエラー(受注取消不可)：エラーコード4001を返す。受注取消不可のデータ。
    CANCEL_QUANTITY_IMPOSSIBLE_ERROR(4001, "CancelQuantitiyImpossibleException", "ERROR_ID_420"),
    // リクエストエラー(受注取消数量1)：エラーコード4002を返す。受注取消数量が納品可否判断情報の注文数を超えている。
    CANCEL_QUANTITY_EXCEEDED_ERROR(4002, "CancelQuantitiyExceededException", "ERROR_ID_421"),
    // リクエストエラー(受注取消数量2)：エラーコード4003を返す。受注取消数量が1以上の整数でない。
    CANCEL_QUANTITY_INVALID_ERROR(4003, "CancelQuantitiyInvalidException", "ERROR_ID_422"),
    // リクエストエラー(返品不可)：エラーコード4004を返す。返品不可のデータ。
    RETURN_QUANTITY_IMPOSSIBLE_ERROR(4004, "ReturnQuantitiyImpossibleException", "ERROR_ID_423"),
    // リクエストエラー(返品数量1)：エラーコード4005を返す。返品数量がSP品販売実績の販売数を超えている。
    RETURN_QUANTITY_EXCEEDED_ERROR(4005, "ReturnQuantityExceededException", "ERROR_ID_424"),
    // リクエストエラー(返品数量2)：エラーコード4006を返す。返品数量が1以上の整数でない。
    RETURN_QUANTITY_INVALID_ERROR(4006, "ReturnQuantitiyInvalidException", "ERROR_ID_425");
    // 状況一覧 END

    @Getter
    private final Integer internalErrorCode;
    // エラーメッセージ
    @Getter
    private final String errorMessage;

    // エラーID
    @Getter
    private final String errorId;

    public boolean is4xxClientError() {
        return (this.getInternalErrorCode() % 100 == 4);
    }

}