
package io.angularpay.crypto.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchedulerServiceRequest {

    private String description;

    @JsonProperty("action_endpoint")
    private String actionEndpoint;

    private String payload;

    @JsonProperty("run_at")
    private String runAt;
}
