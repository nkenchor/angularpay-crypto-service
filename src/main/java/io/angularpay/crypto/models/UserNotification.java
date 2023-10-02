package io.angularpay.crypto.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class UserNotification {

    @NotEmpty
    @JsonProperty("created_on")
    private String createdOn;

    @NotEmpty
    private String reference;

    @NotEmpty
    @JsonProperty("service_code")
    private String serviceCode;

    @NotEmpty
    @JsonProperty("user_reference")
    private String userReference;

    @NotNull
    private UserNotificationType type;

    @NotEmpty
    private String payload;

    @NotEmpty
    private String summary;

    @NotEmpty
    private String attributes;
}
