
package io.angularpay.crypto.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class PaymentRequest {

    @NotEmpty
    @JsonProperty("bank_account_reference")
    private String bankAccountReference;
}
