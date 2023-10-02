package io.angularpay.crypto.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserNotificationRequestPayload {

    @JsonProperty("request_reference")
    private String requestReference;
}
