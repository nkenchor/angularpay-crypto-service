package io.angularpay.crypto.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TimeToLiveModel {

    @JsonProperty("service_code")
    private String serviceCode;
    @JsonProperty("request_created_on")
    private String requestCreatedOn;
    @JsonProperty("request_reference")
    private String requestReference;
    @JsonProperty("investment_reference")
    private String investmentReference;
    @JsonProperty("deletion_link")
    private String deletionLink;
}
