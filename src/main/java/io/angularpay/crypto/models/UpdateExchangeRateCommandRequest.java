package io.angularpay.crypto.models;

import io.angularpay.crypto.domain.ExchangeRate;
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
public class UpdateExchangeRateCommandRequest extends AccessControl {

    @NotEmpty
    private String requestReference;

    @NotNull
    @Valid
    private ExchangeRate exchangeRate;

    UpdateExchangeRateCommandRequest(AuthenticatedUser authenticatedUser) {
        super(authenticatedUser);
    }
}
