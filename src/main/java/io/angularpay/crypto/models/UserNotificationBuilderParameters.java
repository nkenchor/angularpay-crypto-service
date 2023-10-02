package io.angularpay.crypto.models;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserNotificationBuilderParameters<T, U> {

    private String userReference;
    private U request;
    private T commandResponse;
    private UserNotificationType type;
}
