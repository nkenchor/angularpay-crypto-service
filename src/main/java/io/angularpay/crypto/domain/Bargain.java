
package io.angularpay.crypto.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Bargain {

    @JsonProperty("accepted_bargain_reference")
    private String acceptedBargainReference;
    private List<Offer> offers;

}
