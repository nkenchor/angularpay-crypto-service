
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
public class Investor {

    private Amount amount;
    @JsonProperty("bank_account_reference")
    private String bankAccountReference;
    @JsonProperty("created_on")
    private String createdOn;
    @JsonProperty("deleted_by")
    private DeletedBy deletedBy;
    @JsonProperty("deleted_on")
    private String deletedOn;
    @JsonProperty("investment_status")
    private InvestmentStatus investmentStatus;
    @JsonProperty("is_deleted")
    private boolean deleted;
    private String reference;
    @JsonProperty("user_reference")
    private String userReference;
    private String comment;
}
