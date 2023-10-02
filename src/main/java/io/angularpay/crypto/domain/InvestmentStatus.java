
package io.angularpay.crypto.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentStatus {

    private InvestmentTransactionStatus status;
    @JsonProperty("transaction_datetime")
    private String transactionDatetime;
    @JsonProperty("transaction_reference")
    private String transactionReference;
}
