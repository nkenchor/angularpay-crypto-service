
package io.angularpay.crypto.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.angularpay.crypto.domain.Amount;
import io.angularpay.crypto.domain.ExchangeRate;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class CreateRequest {

    @NotNull
    @Valid
    private Amount amount;

    @NotEmpty
    @JsonProperty("beneficiary_reference")
    private String beneficiaryReference;

    @NotNull
    @Valid
    @JsonProperty("exchange_rate")
    private ExchangeRate exchangeRate;
}
