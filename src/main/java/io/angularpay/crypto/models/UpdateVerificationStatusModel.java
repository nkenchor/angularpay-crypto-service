package io.angularpay.crypto.models;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class UpdateVerificationStatusModel extends AccessControl {

    @NotEmpty
    private String requestReference;

    @NotNull
    private Boolean verified;

    UpdateVerificationStatusModel(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
