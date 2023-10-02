package io.angularpay.crypto.models;

import io.angularpay.crypto.domain.DeletedBy;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class RemoveInvestorCommandRequest extends AccessControl {

    @NotEmpty
    private String requestReference;

    @NotEmpty
    private String investmentReference;

    @NotNull
    private DeletedBy deletedBy;

    RemoveInvestorCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
