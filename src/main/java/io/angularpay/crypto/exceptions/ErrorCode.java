package io.angularpay.crypto.exceptions;

import lombok.Getter;

@Getter
public enum ErrorCode {
    INVALID_JSON("The JSON payload is invalid"),
    INVALID_MESSAGE_ERROR("The message format read from the given topic is invalid"),
    CIPHER_ERROR("Cipher operation failed. The cipher reference or signature provided is probably invalid"),
    VALIDATION_ERROR("The request has validation errors"),
    REQUEST_REMOVED_ERROR("You cannot performed this action on a request that has already been removed"),
    REQUEST_COMPLETED_ERROR("You cannot performed this action on a request that has already been completed"),
    REQUEST_CANCELLED_ERROR("You cannot performed this action on a request that has already been cancelled"),
    TARGET_AMOUNT_BOUNDS_ERROR("The investment amount plus the running total exceeds the target amount"),
    REQUEST_NOT_FOUND("The requested resource was NOT found"),
    GENERIC_ERROR("Generic error occurred. See stacktrace for details"),
    SCHEDULER_SERVICE_ERROR("Unable to create scheduled task. Please check scheduler-service logs for details."),
    AUTHORIZATION_ERROR("You do NOT have adequate permission to access this resource"),
    NO_PRINCIPAL("Principal identifier NOT provided", 500);

    private final String defaultMessage;
    private final int defaultHttpStatus;

    ErrorCode(String defaultMessage) {
        this(defaultMessage, 400);
    }

    ErrorCode(String defaultMessage, int defaultHttpStatus) {
        this.defaultMessage = defaultMessage;
        this.defaultHttpStatus = defaultHttpStatus;
    }
}
