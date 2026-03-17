package com.collaboportal.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum InternalErrorCode {
    UNDEINED_ERROR(500, "Undefined", "ERROR_ID_000"),
    ATTESTATION_CERTIFICATE_ERROR(500, "NotAuthenticated", "ERROR_ID_001"),
    VALIDATION_ERROR(400, "ValidationException", "ERROR_ID_002"),
    RECORD_NOT_FOUND_ERROR(404, "RecordNotFoundException", "ERROR_ID_003"),
    OPTIMISTIC_LOCKING_FAILURE_ERROR(500, "OptimisticLockingFailureException", "ERROR_ID_004"),
    SYSTEM_ERROR(500, "SystemException", "ERROR_ID_100"),
    AUTHORIZATION_ERROR(401, "AuthorizationException", "ERROR_ID_101"),
    PATIENT_ID_DUPLICATE_ERROR(409, "PatientIdDuplicateException", "ERROR_ID_200");
    private final int httpStatus;
    private final String errorMessage;
    private final String errorId;
    public Integer getInternalErrorCode() { return httpStatus; }
}
