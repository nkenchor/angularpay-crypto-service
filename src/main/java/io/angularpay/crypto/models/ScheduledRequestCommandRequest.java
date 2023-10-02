package io.angularpay.crypto.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ScheduledRequestCommandRequest extends AccessControl {

    @NotEmpty
    @JsonProperty("run_at")
    private String runAt;

    @NotNull
    @Valid
    private CreateRequest createRequest;

    ScheduledRequestCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
