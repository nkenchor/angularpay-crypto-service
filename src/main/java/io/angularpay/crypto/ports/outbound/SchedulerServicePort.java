package io.angularpay.crypto.ports.outbound;

import io.angularpay.crypto.models.SchedulerServiceRequest;
import io.angularpay.crypto.models.SchedulerServiceResponse;

import java.util.Map;
import java.util.Optional;

public interface SchedulerServicePort {
    Optional<SchedulerServiceResponse> createScheduledRequest(SchedulerServiceRequest request, Map<String, String> headers);
}
