
package io.angularpay.crypto.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserInvestmentModel {

    @JsonProperty("investment_reference")
    private String investmentReference;
    @JsonProperty("request_created_on")
    private String requestCreatedOn;
    @JsonProperty("request_reference")
    private String requestReference;
    @JsonProperty("user_reference")
    private String userReference;
}
