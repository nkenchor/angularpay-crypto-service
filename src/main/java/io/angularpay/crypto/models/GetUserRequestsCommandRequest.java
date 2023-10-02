package io.angularpay.crypto.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class GetUserRequestsCommandRequest extends AccessControl {

    @NotNull
    @Valid
    private Paging paging;

    GetUserRequestsCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
