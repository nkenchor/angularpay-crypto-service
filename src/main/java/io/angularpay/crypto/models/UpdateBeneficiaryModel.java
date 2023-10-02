package io.angularpay.crypto.models;

import io.angularpay.crypto.domain.Beneficiary;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class UpdateBeneficiaryModel extends AccessControl {

    @NotEmpty
    private String requestReference;

    @NotNull
    @Valid
    private Beneficiary beneficiary;

    UpdateBeneficiaryModel(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
